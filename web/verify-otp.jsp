<%-- 
    Document   : verify-otp
    Created on : June 1, 2026
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Xác Minh OTP - E-Vehicle Rental</title>
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                min-height: 100vh;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                color: #172033;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 28px;
                background:
                    radial-gradient(circle at 16% 12%, rgba(205, 164, 82, 0.2), transparent 28%),
                    radial-gradient(circle at 86% 20%, rgba(58, 191, 184, 0.16), transparent 30%),
                    linear-gradient(135deg, #08111f 0%, #111a2c 46%, #f4f0e8 46%, #f8f6f2 100%);
                background-attachment: fixed;
            }

            .verify-shell {
                width: min(940px, 100%);
                display: grid;
                grid-template-columns: 1fr 430px;
                gap: 24px;
                align-items: stretch;
            }

            .brand-panel,
            .verify-container {
                border-radius: 8px;
                border: 1px solid rgba(218, 183, 99, 0.22);
                box-shadow: 0 24px 70px rgba(8, 17, 31, 0.28);
                overflow: hidden;
            }

            .brand-panel {
                position: relative;
                color: white;
                padding: 38px;
                min-height: 520px;
                background:
                    linear-gradient(135deg, rgba(15, 28, 49, 0.96), rgba(47, 61, 91, 0.94)),
                    linear-gradient(135deg, #d6a94e, #3abfb8);
            }

            .brand-panel::after {
                content: "";
                position: absolute;
                right: -70px;
                bottom: -70px;
                width: 260px;
                height: 260px;
                border-radius: 50%;
                border: 1px solid rgba(218, 183, 99, 0.34);
            }

            .brand-mark {
                font-size: 26px;
                font-weight: 800;
                margin-bottom: 90px;
            }

            .brand-panel h2 {
                max-width: 440px;
                font-size: 38px;
                line-height: 1.18;
                margin-bottom: 18px;
            }

            .brand-panel p {
                max-width: 420px;
                color: rgba(255, 255, 255, 0.78);
                line-height: 1.7;
                font-size: 15px;
            }

            .verify-container {
                background: rgba(255, 255, 255, 0.94);
                padding: 40px;
                align-self: center;
                text-align: center;
            }

            .verify-container h1 {
                color: #111827;
                margin-bottom: 10px;
                font-size: 28px;
                font-weight: 800;
            }

            .verify-container .subtitle {
                color: #566070;
                margin-bottom: 24px;
                font-size: 14px;
                line-height: 1.7;
            }

            .info-box {
                background: linear-gradient(180deg, #ffffff 0%, #f7f4ee 100%);
                padding: 15px;
                border-radius: 8px;
                margin-bottom: 20px;
                border: 1px solid rgba(218, 183, 99, 0.48);
                color: #8f621b;
                text-align: left;
                font-size: 13px;
                line-height: 1.6;
            }

            .form-group {
                margin-bottom: 20px;
            }

            label {
                display: block;
                color: #111827;
                font-weight: 700;
                margin-bottom: 8px;
                font-size: 14px;
                text-align: left;
            }

            input[type="text"] {
                width: 100%;
                padding: 15px;
                border: 1px solid rgba(17, 24, 39, 0.14);
                border-radius: 7px;
                background: rgba(255, 255, 255, 0.84);
                color: #111827;
                font-size: 22px;
                text-align: center;
                letter-spacing: 8px;
                font-weight: 800;
                transition: border-color 0.25s, box-shadow 0.25s, background 0.25s;
                font-family: 'Courier New', monospace;
            }

            input[type="text"]:focus {
                outline: none;
                border-color: rgba(214, 169, 78, 0.85);
                box-shadow: 0 0 0 4px rgba(214, 169, 78, 0.16);
                background: #ffffff;
            }

            .error-message {
                background-color: #fee2e2;
                color: #7f1d1d;
                padding: 12px 14px;
                border-radius: 7px;
                margin-bottom: 20px;
                display: <%= request.getAttribute("error") != null ? "block" : "none" %>;
                border: 1px solid #fecaca;
                line-height: 1.5;
                text-align: left;
                font-size: 14px;
            }

            .verify-btn {
                width: 100%;
                padding: 13px;
                color: #09111f;
                border: 1px solid rgba(218, 183, 99, 0.55);
                border-radius: 7px;
                background: linear-gradient(135deg, #f8df9d 0%, #d6a94e 100%);
                font-size: 16px;
                font-weight: 800;
                cursor: pointer;
                box-shadow: 0 12px 28px rgba(180, 122, 31, 0.2);
                transition: transform 0.25s, box-shadow 0.25s, opacity 0.25s;
            }

            .verify-btn:hover {
                transform: translateY(-2px);
                box-shadow: 0 16px 36px rgba(180, 122, 31, 0.3);
            }

            .verify-btn:disabled {
                opacity: 0.55;
                cursor: not-allowed;
                transform: none;
            }

            .back-link {
                text-align: center;
                margin-top: 20px;
            }

            .back-link a {
                color: #b47a1f;
                text-decoration: none;
                font-size: 14px;
                font-weight: 800;
            }

            .back-link a:hover {
                text-decoration: underline;
            }

            .timer {
                color: #8f621b;
                font-weight: 800;
                margin-top: 15px;
                font-size: 14px;
            }

            @media (max-width: 860px) {
                .verify-shell {
                    grid-template-columns: 1fr;
                }

                .brand-panel {
                    min-height: auto;
                }

                .brand-mark {
                    margin-bottom: 34px;
                }
            }

            @media (max-width: 520px) {
                body {
                    padding: 16px;
                }

                .brand-panel,
                .verify-container {
                    padding: 26px;
                }

                .brand-panel h2 {
                    font-size: 30px;
                }

                input[type="text"] {
                    letter-spacing: 5px;
                }
            }
        </style>
        <script>
            let timeLeft = 5 * 60;

            function startTimer() {
                const timerElement = document.getElementById('timer');

                const timer = setInterval(() => {
                    timeLeft--;
                    const minutes = Math.floor(timeLeft / 60);
                    const seconds = timeLeft % 60;

                    timerElement.textContent = `Mã OTP hết hạn trong: ${minutes}:${seconds.toString().padStart(2, '0')}`;

                    if (timeLeft <= 0) {
                        clearInterval(timer);
                        timerElement.textContent = 'Mã OTP đã hết hạn. Vui lòng đăng ký lại.';
                        document.querySelector('button[type="submit"]').disabled = true;
                    }
                }, 1000);
            }

            window.onload = () => {
                startTimer();
            };
        </script>
    </head>
    <body>
        <div class="verify-shell">
            <section class="brand-panel">
                <div class="brand-mark">🚗 E-Vehicle Rental</div>
                <h2>Xác minh để bắt đầu trải nghiệm.</h2>
                <p>Mã OTP đã được gửi tới email đăng ký. Hoàn tất bước này để kích hoạt tài khoản của bạn.</p>
            </section>

            <div class="verify-container">
                <h1>Xác Minh Email</h1>
                <p class="subtitle">Vui lòng nhập mã OTP gồm 6 chữ số để hoàn tất đăng ký.</p>

                <div class="info-box">
                    ⏱️ <strong>Lưu ý:</strong> Mã OTP có hiệu lực trong 5 phút
                </div>

                <% 
                    String error = (String) request.getAttribute("error");
                    if (error != null && !error.isEmpty()) {
                %>
                    <div class="error-message">
                        <%= error %>
                    </div>
                <% } %>

                <form action="verify-otp" method="POST" accept-charset="UTF-8">
                    <div class="form-group">
                        <label for="otp">Nhập Mã OTP (6 chữ số) *</label>
                        <input 
                            type="text" 
                            id="otp" 
                            name="otp" 
                            placeholder="000000"
                            maxlength="6"
                            pattern="\d{6}"
                            required
                            autofocus
                        >
                    </div>

                    <button type="submit" class="verify-btn">Xác Minh</button>

                    <div class="timer" id="timer">
                        Mã OTP hết hạn trong: 5:00
                    </div>
                </form>

                <div class="back-link">
                    <a href="<%= request.getContextPath() %>?action=register">← Quay lại Đăng Ký</a>
                </div>
            </div>
        </div>
    </body>
</html>
