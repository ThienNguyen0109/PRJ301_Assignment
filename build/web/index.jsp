<%-- 
    Document   : index
    Created on : May 28, 2026, 7:46:31 AM
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Home - E-Vehicle Rental</title>
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
            
            .home-container {
                background: white;
                border-radius: 10px;
                box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
                width: 100%;
                max-width: 600px;
                padding: 60px 40px;
                text-align: center;
            }
            
            .home-container h1 {
                color: #333;
                margin-bottom: 20px;
                font-size: 36px;
            }
            
            .home-container p {
                color: #666;
                margin-bottom: 30px;
                font-size: 16px;
                line-height: 1.6;
            }
            
            .button-group {
                display: flex;
                gap: 15px;
                justify-content: center;
                flex-wrap: wrap;
            }
            
            .btn {
                padding: 12px 30px;
                border: none;
                border-radius: 5px;
                font-size: 16px;
                font-weight: 600;
                cursor: pointer;
                text-decoration: none;
                transition: transform 0.2s, box-shadow 0.2s;
                display: inline-block;
            }
            
            .btn-login {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
            }
            
            .btn-login:hover {
                transform: translateY(-2px);
                box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
            }
            
            .btn-register {
                background: #f0f0f0;
                color: #333;
                border: 2px solid #667eea;
            }
            
            .btn-register:hover {
                background: #f9f9f9;
                transform: translateY(-2px);
            }
        </style>
    </head>
    <body>
        <div class="home-container">
            <h1>E-Vehicle Rental System</h1>
            <p>Welcome to our vehicle rental platform. 
                <br/>Easily book and manage your vehicle rentals with us.</p>
            
            <div class="button-group">
                <a href="<%= request.getContextPath() %>?page=login" class="btn btn-login">Login</a>
                <a href="<%= request.getContextPath() %>?page=register" class="btn btn-register">Sign Up</a>
            </div>
        </div>
    </body>
</html>
