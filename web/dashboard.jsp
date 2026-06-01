<%-- 
    Document   : dashboard
    Created on : May 28, 2026
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Dashboard - E-Vehicle Rental</title>
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }
            
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background: #f5f5f5;
            }
            
            .navbar {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 15px 30px;
                display: flex;
                justify-content: space-between;
                align-items: center;
                box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            }
            
            .navbar h1 {
                font-size: 24px;
            }
            
            .navbar-menu {
                display: flex;
                gap: 20px;
                align-items: center;
            }
            
            .navbar a {
                color: white;
                text-decoration: none;
                padding: 8px 15px;
                border-radius: 5px;
                transition: background 0.3s;
            }
            
            .navbar a:hover {
                background: rgba(255,255,255,0.2);
            }
            
            .logout-btn {
                background: #ff6b6b;
                padding: 8px 15px;
                border-radius: 5px;
                cursor: pointer;
                text-decoration: none;
                transition: background 0.3s;
            }
            
            .logout-btn:hover {
                background: #ff5252;
            }
            
            .container {
                max-width: 1200px;
                margin: 30px auto;
                padding: 0 20px;
            }
            
            .welcome-card {
                background: white;
                padding: 30px;
                border-radius: 10px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                margin-bottom: 20px;
            }
            
            .welcome-card h2 {
                color: #333;
                margin-bottom: 10px;
            }
            
            .welcome-card p {
                color: #666;
                line-height: 1.6;
            }
            
            .user-info {
                background: white;
                padding: 20px;
                border-radius: 10px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            }
            
            .user-info h3 {
                color: #333;
                margin-bottom: 15px;
                border-bottom: 2px solid #667eea;
                padding-bottom: 10px;
            }
            
            .info-row {
                display: flex;
                padding: 10px 0;
                border-bottom: 1px solid #eee;
            }
            
            .info-row:last-child {
                border-bottom: none;
            }
            
            .info-label {
                font-weight: 600;
                width: 150px;
                color: #667eea;
            }
            
            .info-value {
                color: #333;
            }
        </style>
    </head>
    <body>
        <%
            // Check if user is logged in
            Object userObj = session.getAttribute("user");
            if (userObj == null) {
                response.sendRedirect(request.getContextPath() + "?page=login");
                return;
            }
            
            String userEmail = (String) session.getAttribute("userEmail");
            String userName = (String) session.getAttribute("userName");
            String userRole = (String) session.getAttribute("userRole");
        %>
        
        <div class="navbar">
            <h1>E-Vehicle Rental System</h1>
            <div class="navbar-menu">
                <a href="<%= request.getContextPath() %>?page=dashboard">Dashboard</a>
                <a href="<%= request.getContextPath() %>?page=profile">Profile</a>
                <a href="<%= request.getContextPath() %>/logout" class="logout-btn">Logout</a>
            </div>
        </div>
        
        <div class="container">
            <div class="welcome-card">
                <h2>Welcome, <%= userName %>!</h2>
                <p>You are successfully logged in to the E-Vehicle Rental System.</p>
            </div>
            
            <div class="user-info">
                <h3>Your Information</h3>
                <div class="info-row">
                    <span class="info-label">Email:</span>
                    <span class="info-value"><%= userEmail %></span>
                </div>
                <div class="info-row">
                    <span class="info-label">Full Name:</span>
                    <span class="info-value"><%= userName %></span>
                </div>
                <div class="info-row">
                    <span class="info-label">Role:</span>
                    <span class="info-value"><%= userRole %></span>
                </div>
            </div>
        </div>
    </body>
</html>
