<%-- 
    Document   : login
    Created on : May 28, 2026, 7:47:20 AM
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đăng Nhập - E-Vehicle Rental</title>
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
            }
            
            .login-container {
                background: white;
                border-radius: 10px;
                box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
                width: 100%;
                max-width: 400px;
                padding: 40px;
            }
            
            .login-container h1 {
                color: #333;
                margin-bottom: 10px;
                text-align: center;
                font-size: 24px;
            }
            
            .login-container p {
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
            
            input[type="email"],
            input[type="password"] {
                width: 100%;
                padding: 12px;
                border: 1px solid #ddd;
                border-radius: 5px;
                font-size: 14px;
                transition: border-color 0.3s;
            }
            
            input[type="email"]:focus,
            input[type="password"]:focus {
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
            
            .login-btn {
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
            
            .login-btn:hover {
                transform: translateY(-2px);
                box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
            }
            
            .login-btn:active {
                transform: translateY(0);
            }
            
            .footer-links {
                text-align: center;
                margin-top: 20px;
                font-size: 14px;
            }
            
            .footer-links a {
                color: #667eea;
                text-decoration: none;
                margin: 0 10px;
            }
            
            .footer-links a:hover {
                text-decoration: underline;
            }
        </style>
    </head>
    <body>
        <div class="login-container">
            <h1>Đăng Nhập</h1>
            <p>E-Vehicle Rental System</p>
            
            <% 
                String error = (String) request.getAttribute("error");
                if (error != null && !error.isEmpty()) {
            %>
                <div class="error-message">
                    <%= error %>
                </div>
            <% } %>
            
            <form action="login" method="POST">
                <div class="form-group">
                    <label for="email">Email</label>
                    <input 
                        type="email" 
                        id="email" 
                        name="email" 
                        placeholder="Nhập email của bạn"
                        required
                        value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>"
                    >
                </div>
                
                <div class="form-group">
                    <label for="password">Mật Khẩu</label>
                    <input 
                        type="password" 
                        id="password" 
                        name="password" 
                        placeholder="Nhập mật khẩu của bạn"
                        required
                    >
                </div>
                
                <button type="submit" class="login-btn">Đăng Nhập</button>
            </form>
            
            <div class="footer-links">
                <a href="#forgot">Quên mật khẩu?</a> | 
                <a href="#signup">Đăng ký</a>
            </div>
        </div>
    </body>
</html>
