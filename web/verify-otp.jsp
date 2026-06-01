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
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                min-height: 100vh;
                display: flex;
                justify-content: center;
                align-items: center;
                padding: 20px;
            }
            
            .verify-container {
                background: white;
                border-radius: 10px;
                box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
                width: 100%;
                max-width: 450px;
                padding: 40px;
                text-align: center;
            }
            
            .verify-container h1 {
                color: #333;
                margin-bottom: 10px;
                font-size: 28px;
            }
            
            .verify-container p {
                color: #666;
                margin-bottom: 30px;
                font-size: 14px;
                line-height: 1.6;
            }
            
            .info-box {
                background: #f0f4ff;
                padding: 15px;
                border-radius: 5px;
                margin-bottom: 20px;
                border-left: 4px solid #667eea;
                text-align: left;
                font-size: 13px;
            }
            
            .form-group {
                margin-bottom: 20px;
            }
            
            label {
                display: block;
                color: #333;
                font-weight: 600;
                margin-bottom: 8px;
                font-size: 14px;
            }
            
            input[type="text"] {
                width: 100%;
                padding: 15px;
                border: 2px solid #ddd;
                border-radius: 5px;
                font-size: 18px;
                text-align: center;
                letter-spacing: 5px;
                font-weight: bold;
                transition: border-color 0.3s;
                font-family: 'Courier New', monospace;
            }
            
            input[type="text"]:focus {
                outline: none;
                border-color: #667eea;
                box-shadow: 0 0 5px rgba(102, 126, 234, 0.3);
            }
            
            .error-message {
                background-color: #f8d7da;
                color: #721c24;
                padding: 12px;
                border-radius: 5px;
                margin-bottom: 20px;
                display: <%= request.getAttribute("error") != null ? "block" : "none" %>;
                border: 1px solid #f5c6cb;
            }
            
            .verify-btn {
                width: 100%;
                padding: 12px;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                border: none;
                border-radius: 5px;
                font-size: 16px;
                font-weight: 600;
                cursor: pointer;
                transition: transform 0.2s, box-shadow 0.2s;
            }
            
            .verify-btn:hover {
                transform: translateY(-2px);
                box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
            }
            
            .verify-btn:active {
                transform: translateY(0);
            }
            
            .back-link {
                text-align: center;
                margin-top: 20px;
            }
            
            .back-link a {
                color: #667eea;
                text-decoration: none;
                font-size: 14px;
            }
            
            .back-link a:hover {
                text-decoration: underline;
            }
            
            .timer {
                color: #ff6b6b;
                font-weight: 600;
                margin-top: 15px;
                font-size: 14px;
            }
        </style>
        <script>
            // OTP Timer - 5 minutes
            let timeLeft = 5 * 60; // 5 minutes in seconds
            
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
        <div class="verify-container">
            <h1>Xác Minh Email</h1>
            <p>Mã OTP đã được gửi đến email của bạn.<br/>Vui lòng nhập mã OTP để hoàn tất đăng ký.</p>
            
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
                <a href="<%= request.getContextPath() %>?page=register">← Quay lại Đăng Ký</a>
            </div>
        </div>
    </body>
</html>
