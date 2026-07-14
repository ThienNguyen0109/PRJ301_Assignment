(function () {
    function onlyDigits(value) {
        return (value || '').replace(/[^\d]/g, '');
    }

    function formatMoney(value) {
        var digits = onlyDigits(String(value || ''));
        if (!digits) {
            return '';
        }
        return digits.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    }

    function validate(display, digits) {
        if (!display.required && !digits) {
            display.setCustomValidity('');
            return;
        }
        if (display.required && !digits) {
            display.setCustomValidity('Vui l\u00f2ng nh\u1eadp s\u1ed1 ti\u1ec1n.');
            return;
        }

        var amount = parseInt(digits || '0', 10);
        var min = parseInt(display.getAttribute('data-money-min') || '', 10);
        var max = parseInt(display.getAttribute('data-money-max') || '', 10);

        if (!isNaN(min) && amount < min) {
            display.setCustomValidity('S\u1ed1 ti\u1ec1n t\u1ed1i thi\u1ec3u l\u00e0 ' + formatMoney(min) + ' VND.');
        } else if (!isNaN(max) && amount > max) {
            display.setCustomValidity('S\u1ed1 ti\u1ec1n t\u1ed1i \u0111a l\u00e0 ' + formatMoney(max) + ' VND.');
        } else {
            display.setCustomValidity('');
        }
    }

    function syncMoneyInput(display) {
        var targetId = display.getAttribute('data-money-target');
        var target = targetId ? document.getElementById(targetId) : null;
        var digits = onlyDigits(display.value);

        if (target) {
            target.value = digits;
        }
        display.value = formatMoney(digits);
        validate(display, digits);
    }

    function bindForm(form) {
        if (!form || form.dataset.moneyFormatterBound) {
            return;
        }
        form.dataset.moneyFormatterBound = 'true';
        form.addEventListener('submit', function () {
            Array.prototype.forEach.call(form.querySelectorAll('[data-money-target]'), syncMoneyInput);
        });
    }

    function init() {
        Array.prototype.forEach.call(document.querySelectorAll('[data-money-target]'), function (display) {
            syncMoneyInput(display);
            display.addEventListener('input', function () {
                syncMoneyInput(display);
            });
            display.addEventListener('blur', function () {
                syncMoneyInput(display);
            });
            bindForm(display.form);
        });
    }

    document.addEventListener('DOMContentLoaded', init);
    window.formatMoneyInputValue = formatMoney;
    window.syncMoneyInput = syncMoneyInput;
})();
