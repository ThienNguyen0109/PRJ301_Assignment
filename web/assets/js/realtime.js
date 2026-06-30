(function () {
    var config = window.EVR_REALTIME;
    if (!config || !config.role || !config.accountId || !window.WebSocket) {
        return;
    }

    var socket;
    var reconnectTimer;
    var reconnectDelay = 2500;

    function connect() {
        var protocol = window.location.protocol === 'https:' ? 'wss://' : 'ws://';
        var basePath = config.contextPath || '';
        var url = protocol + window.location.host + basePath + '/ws/realtime'
            + '?role=' + encodeURIComponent(config.role)
            + '&accountId=' + encodeURIComponent(config.accountId);

        socket = new WebSocket(url);
        socket.onmessage = function (message) {
            try {
                handleEvent(JSON.parse(message.data));
            } catch (ignore) {
                // Ignore invalid realtime payloads.
            }
        };
        socket.onclose = scheduleReconnect;
        socket.onerror = function () {
            try {
                socket.close();
            } catch (ignore) {
                // Socket is already closing.
            }
        };
    }

    function scheduleReconnect() {
        window.clearTimeout(reconnectTimer);
        reconnectTimer = window.setTimeout(connect, reconnectDelay);
    }

    function handleEvent(event) {
        if (!event || !event.type) {
            return;
        }
        showToast(event.title || 'System update', event.message || 'New data is available.');
        window.dispatchEvent(new CustomEvent('evr:realtime', { detail: event }));
        updateLiveRegions(event.type);
    }

    function updateLiveRegions(type) {
        var action = new URLSearchParams(window.location.search).get('action') || '';
        if (/WALLET_BALANCE_CHANGED/.test(type)) {
            refreshWalletBalance();
        }
        if (action === 'staff-dashboard' && /RENTAL_|PICKUP_|RETURN_|VEHICLE_|MAINTENANCE_|INCIDENT_/.test(type)) {
            refreshStaffDashboard();
        }
        if (action === 'admin-dashboard' && /ADMIN_|PAYMENT_|RENTAL_|VEHICLE_|MAINTENANCE_|INCIDENT_|EXTRA_CHARGE/.test(type)) {
            refreshAdminDashboard();
        }
        if (/^admin-(financial-reports|station-performance|model-performance)$/.test(action)
                && /ADMIN_|PAYMENT_|RENTAL_|VEHICLE_|MAINTENANCE_|INCIDENT_|EXTRA_CHARGE/.test(type)) {
            refreshAdminReport(action);
        }
    }

    function apiUrl(type, extraParams) {
        var params = new URLSearchParams(extraParams || {});
        params.set('type', type);
        return (config.contextPath || '') + '/api/realtime-data?' + params.toString();
    }

    function fetchJson(url) {
        return fetch(url, {
            method: 'GET',
            credentials: 'same-origin',
            headers: { 'Accept': 'application/json' }
        }).then(function (response) {
            if (!response.ok) {
                throw new Error('Realtime API failed');
            }
            return response.json();
        });
    }

    function refreshWalletBalance() {
        var targets = document.querySelectorAll('[data-realtime-wallet-balance]');
        if (!targets.length) {
            return;
        }
        fetchJson(apiUrl('wallet')).then(function (data) {
            targets.forEach(function (target) {
                target.textContent = data.balanceText || '0.00 VND';
                target.classList.add('evr-live-updated');
                window.setTimeout(function () { target.classList.remove('evr-live-updated'); }, 900);
            });
        }).catch(function () {});
    }

    function refreshStaffDashboard() {
        if (!document.querySelector('[data-staff-stat]') && !document.querySelector('[data-staff-activities]')) {
            return;
        }
        fetchJson(apiUrl('staff-dashboard')).then(function (data) {
            setText('[data-staff-stat="waitingForPickup"]', data.waitingForPickup);
            setText('[data-staff-stat="currentlyRented"]', data.currentlyRented);
            setText('[data-staff-stat="waitingForReturn"]', data.waitingForReturn);
            setText('[data-staff-stat="underMaintenance"]', data.underMaintenance);
            renderStaffActivities(data.recentActivities || []);
        }).catch(function () {});
    }

    function renderStaffActivities(activities) {
        var tbody = document.querySelector('[data-staff-activities]');
        if (!tbody) {
            return;
        }
        tbody.innerHTML = '';
        if (!activities.length) {
            var empty = document.createElement('tr');
            empty.innerHTML = '<td colspan="5" class="empty-state">No recent activities.</td>';
            tbody.appendChild(empty);
            return;
        }
        activities.forEach(function (activity) {
            var row = document.createElement('tr');
            appendCell(row, activity.rentalId, true);
            appendCell(row, activity.customer);
            appendCell(row, activity.vehicle);
            appendCell(row, activity.action);
            appendCell(row, activity.time);
            tbody.appendChild(row);
        });
        pulse(tbody);
    }

    function refreshAdminDashboard() {
        fetchJson(apiUrl('admin-dashboard')).then(function (data) {
            renderAdminStats(data.stats || []);
            renderAdminDashboardRows(data.rows || []);
        }).catch(function () {});
    }

    function refreshAdminReport(action) {
        var params = new URLSearchParams(window.location.search);
        var report = action === 'admin-station-performance'
            ? 'station'
            : (action === 'admin-model-performance' ? 'model' : 'financial');
        params.set('report', report);
        fetchJson(apiUrl('admin-report', params)).then(function (data) {
            renderAdminStats(data.stats || []);
            renderAdminReportRows(data.rows || []);
            renderAdminCharts(report, data.primaryChart || [], data.secondaryChart || []);
        }).catch(function () {});
    }

    function renderAdminStats(stats) {
        var valuesByLabel = {};
        stats.forEach(function (stat) {
            valuesByLabel[normalizeLabel(stat.label)] = stat.value || '';
        });

        document.querySelectorAll('[data-admin-stat]').forEach(function (container) {
            var labelNode = container.querySelector('[data-admin-stat-label]');
            var valueNode = container.querySelector('[data-admin-stat-value]');
            if (!labelNode || !valueNode) {
                return;
            }
            var key = normalizeLabel(labelNode.textContent);
            if (Object.prototype.hasOwnProperty.call(valuesByLabel, key)) {
                valueNode.textContent = valuesByLabel[key];
                pulse(valueNode);
            }
        });
    }

    function normalizeLabel(value) {
        return String(value || '').trim().toLowerCase();
    }

    function renderAdminDashboardRows(rows) {
        var tbody = document.querySelector('[data-admin-dashboard-rows]');
        if (!tbody) {
            return;
        }
        tbody.innerHTML = '';
        rows.forEach(function (cells) {
            var row = document.createElement('tr');
            cells.forEach(function (cell, index) {
                var td = document.createElement('td');
                if (index === cells.length - 1) {
                    var chip = document.createElement('span');
                    chip.className = 'status-chip';
                    chip.textContent = cell;
                    td.appendChild(chip);
                } else {
                    td.textContent = cell;
                }
                row.appendChild(td);
            });
            tbody.appendChild(row);
        });
        pulse(tbody);
    }

    function renderAdminReportRows(rows) {
        var tbody = document.querySelector('[data-admin-report-rows]');
        if (!tbody) {
            return;
        }
        tbody.innerHTML = '';
        rows.forEach(function (cells) {
            var row = document.createElement('tr');
            var displayCount = cells.length > 4 ? cells.length - 1 : cells.length;
            for (var i = 0; i < displayCount; i++) {
                var td = document.createElement('td');
                if (i >= 2) {
                    var chip = document.createElement('span');
                    chip.className = 'status-chip';
                    chip.textContent = cells[i];
                    td.appendChild(chip);
                } else {
                    td.textContent = cells[i];
                }
                row.appendChild(td);
            }
            var actionTd = document.createElement('td');
            if (cells.length > displayCount && cells[displayCount]) {
                var link = document.createElement('a');
                link.className = 'admin-button light';
                link.href = (config.contextPath || '') + '/' + cells[displayCount];
                link.textContent = 'View';
                actionTd.appendChild(link);
            } else {
                var span = document.createElement('span');
                span.className = 'admin-button light disabled-link';
                span.textContent = 'View';
                actionTd.appendChild(span);
            }
            row.appendChild(actionTd);
            tbody.appendChild(row);
        });
        pulse(tbody);
    }

    function renderAdminCharts(report, primary, secondary) {
        if (report === 'financial') {
            renderFinancialBars(primary);
            renderLegend('[data-admin-chart="financial-secondary"]', secondary, ['#2563eb', '#06b6d4', '#f59e0b']);
            updateFinancialDonut(secondary);
        } else if (report === 'station') {
            renderStationStacks(primary);
            renderLegend('[data-admin-chart="station-secondary"]', secondary, ['#2563eb', '#06b6d4', '#f59e0b']);
        } else if (report === 'model') {
            renderModelBars(primary);
            renderLegend('[data-admin-chart="model-secondary"]', secondary, ['#ef4444', '#22c55e']);
        }
    }

    function renderFinancialBars(items) {
        var chart = document.querySelector('[data-admin-chart="financial-primary"]');
        if (!chart) return;
        chart.innerHTML = '';
        items.forEach(function (item, index) {
            var wrap = document.createElement('div');
            wrap.className = 'bar-item';
            wrap.title = item.value || '';
            if (item.percent > 0) {
                var value = document.createElement('span');
                value.className = 'bar-value';
                value.textContent = item.value || '';
                wrap.appendChild(value);
            }
            var bar = document.createElement('span');
            bar.className = 'bar' + (index > 8 ? ' hot' : (index > 5 ? ' accent' : ''));
            bar.style.height = (item.percent || 0) + '%';
            wrap.appendChild(bar);
            var label = document.createElement('strong');
            label.textContent = item.label || '';
            wrap.appendChild(label);
            chart.appendChild(wrap);
        });
        pulse(chart);
    }

    function renderStationStacks(items) {
        var chart = document.querySelector('[data-admin-chart="station-primary"]');
        if (!chart) return;
        chart.innerHTML = '';
        items.forEach(function (item) {
            var row = document.createElement('div');
            row.className = 'stack-row';
            row.innerHTML = '<label></label><div class="stack-track"><span class="stack available"></span><span class="stack rented"></span><span class="stack maintenance"></span></div><strong></strong>';
            row.querySelector('label').textContent = item.label || '';
            row.querySelector('.available').style.width = (item.percent || 0) + '%';
            row.querySelector('.rented').style.width = (item.secondaryPercent || 0) + '%';
            row.querySelector('.maintenance').style.width = (item.tertiaryPercent || 0) + '%';
            row.querySelector('strong').textContent = item.value || '';
            chart.appendChild(row);
        });
        pulse(chart);
    }

    function renderModelBars(items) {
        var chart = document.querySelector('[data-admin-chart="model-primary"]');
        if (!chart) return;
        chart.innerHTML = '';
        items.forEach(function (item) {
            var row = document.createElement('div');
            row.className = 'hbar-row';
            row.innerHTML = '<label></label><span><i></i></span><strong></strong>';
            row.querySelector('label').textContent = item.label || '';
            row.querySelector('i').style.width = (item.percent || 0) + '%';
            row.querySelector('strong').textContent = item.value || '';
            chart.appendChild(row);
        });
        pulse(chart);
    }

    function renderLegend(selector, items, colors) {
        var legend = document.querySelector(selector);
        if (!legend) return;
        legend.innerHTML = '';
        items.forEach(function (item, index) {
            var row = document.createElement('div');
            var icon = document.createElement('i');
            icon.style.background = colors[index] || '#f59e0b';
            row.appendChild(icon);
            row.appendChild(document.createTextNode((item.label || '') + ' - ' + (item.percent || 0) + '% (' + (item.value || '') + ')'));
            legend.appendChild(row);
        });
        pulse(legend);
    }

    function updateFinancialDonut(items) {
        var donut = document.querySelector('.finance-donut');
        if (!donut) return;
        var first = items[0] || { percent: 0 };
        var percent = first.percent || 0;
        donut.style.background = percent > 0
            ? 'conic-gradient(#2563eb 0 ' + percent + '%, #06b6d4 ' + percent + '% 100%)'
            : 'conic-gradient(#e2e8f0 0 100%)';
        var text = donut.querySelector('span');
        if (text) text.textContent = percent + '%';
        pulse(donut);
    }

    function setText(selector, value) {
        var element = document.querySelector(selector);
        if (element) {
            element.textContent = value == null ? '0' : value;
            pulse(element);
        }
    }

    function appendCell(row, value, strong) {
        var td = document.createElement('td');
        if (strong) {
            var tag = document.createElement('strong');
            tag.textContent = value || '';
            td.appendChild(tag);
        } else {
            td.textContent = value || '';
        }
        row.appendChild(td);
    }

    function pulse(element) {
        if (!element) return;
        element.classList.add('evr-live-updated');
        window.setTimeout(function () { element.classList.remove('evr-live-updated'); }, 900);
    }

    function showToast(title, message) {
        var stack = document.querySelector('.evr-toast-stack');
        if (!stack) {
            stack = document.createElement('div');
            stack.className = 'evr-toast-stack';
            document.body.appendChild(stack);
            injectStyles();
        }

        var toast = document.createElement('div');
        toast.className = 'evr-toast';
        toast.innerHTML = '<strong></strong><span></span>';
        toast.querySelector('strong').textContent = title;
        toast.querySelector('span').textContent = message;
        stack.appendChild(toast);

        window.setTimeout(function () {
            toast.classList.add('is-leaving');
            window.setTimeout(function () {
                if (toast.parentNode) {
                    toast.parentNode.removeChild(toast);
                }
            }, 260);
        }, 4200);
    }

    function injectStyles() {
        if (document.getElementById('evr-realtime-style')) {
            return;
        }
        var style = document.createElement('style');
        style.id = 'evr-realtime-style';
        style.textContent = ''
            + '.evr-toast-stack{position:fixed;right:22px;bottom:22px;z-index:9999;display:grid;gap:12px;max-width:360px}'
            + '.evr-toast{padding:14px 16px;border-radius:14px;background:#0f172a;color:#fff;box-shadow:0 18px 45px rgba(15,23,42,.25);border:1px solid rgba(255,255,255,.16);animation:evrToastIn .22s ease-out both}'
            + '.evr-toast strong{display:block;font-size:14px;margin-bottom:4px;color:#fde68a}'
            + '.evr-toast span{display:block;font-size:13px;line-height:1.45;color:#e5e7eb}'
            + '.evr-toast.is-leaving{animation:evrToastOut .24s ease-in both}'
            + '.evr-live-updated{animation:evrLivePulse .8s ease-out both}'
            + '@keyframes evrToastIn{from{opacity:0;transform:translateY(12px) scale(.98)}to{opacity:1;transform:translateY(0) scale(1)}}'
            + '@keyframes evrToastOut{to{opacity:0;transform:translateY(10px) scale(.98)}}'
            + '@keyframes evrLivePulse{0%{filter:brightness(1);transform:scale(1)}35%{filter:brightness(1.12);transform:scale(1.015)}100%{filter:brightness(1);transform:scale(1)}}';
        document.head.appendChild(style);
    }

    connect();
})();
