<%-- 
    Document   : register
    Created on : June 1, 2026
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đăng Ký - E-Vehicle Rental</title>
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
            
            .register-container {
                background: white;
                border-radius: 10px;
                box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
                width: 100%;
                max-width: 500px;
                padding: 40px;
            }
            
            .register-container h1 {
                color: #333;
                margin-bottom: 10px;
                text-align: center;
                font-size: 28px;
            }
            
            .register-container p {
                color: #666;
                text-align: center;
                margin-bottom: 30px;
                font-size: 14px;
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
            
            input[type="text"],
            input[type="email"],
            input[type="password"],
            input[type="tel"] {
                width: 100%;
                padding: 12px;
                border: 1px solid #ddd;
                border-radius: 5px;
                font-size: 14px;
                transition: border-color 0.3s;
                font-family: Arial, sans-serif;
            }
            
            input[type="text"]:focus,
            input[type="email"]:focus,
            input[type="password"]:focus,
            input[type="tel"]:focus {
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
            
            .password-group {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 15px;
            }
            
            .register-btn {
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
                margin-top: 10px;
            }
            
            .register-btn:hover {
                transform: translateY(-2px);
                box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
            }
            
            .register-btn:active {
                transform: translateY(0);
            }
            
            .login-link {
                text-align: center;
                margin-top: 20px;
                font-size: 14px;
            }
            
            .login-link a {
                color: #667eea;
                text-decoration: none;
                font-weight: 600;
            }
            
            .login-link a:hover {
                text-decoration: underline;
            }
            
            @media (max-width: 600px) {
                .password-group {
                    grid-template-columns: 1fr;
                }
            }
        </style>
    </head>
    <body>
        <div class="register-container">
            <h1>Đăng Ký</h1>
            <p>Tạo tài khoản E-Vehicle Rental</p>
            
            <% 
                String error = (String) request.getAttribute("error");
                if (error != null && !error.isEmpty()) {
            %>
                <div class="error-message">
                    <%= error %>
                </div>
            <% } %>
            
            <form action="register" method="POST" accept-charset="UTF-8">
                <div class="form-group">
                    <label for="fullName">Họ và Tên *</label>
                    <input 
                        type="text" 
                        id="fullName" 
                        name="fullName" 
                        placeholder="Nhập họ và tên"
                        required
                        value="<%= request.getAttribute("fullName") != null ? request.getAttribute("fullName") : "" %>"
                    >
                </div>
                
                <div class="form-group">
                    <label for="email">Email *</label>
                    <input 
                        type="email" 
                        id="email" 
                        name="email" 
                        placeholder="Nhập email"
                        required
                        value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>"
                    >
                </div>
                
                <div class="form-group">
                    <label for="phone">Số Điện Thoại</label>
                    <input 
                        type="tel" 
                        id="phone" 
                        name="phone" 
                        placeholder="Nhập số điện thoại (10-11 chữ số)"
                        value="<%= request.getAttribute("phone") != null ? request.getAttribute("phone") : "" %>"
                    >
                </div>
                
                <div class="password-group">
                    <div class="form-group">
                        <label for="password">Mật Khẩu *</label>
                        <input 
                            type="password" 
                            id="password" 
                            name="password" 
                            placeholder="Nhập mật khẩu"
                            required
                        >
                    </div>
                    
                    <div class="form-group">
                        <label for="confirmPassword">Xác Nhận Mật Khẩu *</label>
                        <input 
                            type="password" 
                            id="confirmPassword" 
                            name="confirmPassword" 
                            placeholder="Xác nhận mật khẩu"
                            required
                        >
                    </div>
                </div>
                
                <button type="submit" class="register-btn">Đăng Ký</button>
            </form>
            
            <div class="login-link">
                Đã có tài khoản? <a href="<%= request.getContextPath() %>?page=login">Đăng Nhập</a>
            </div>
        </div>
    </body>
</html>
