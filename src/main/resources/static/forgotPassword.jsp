<html>
<head>
    <title>Spedire</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
            text-align: center;
        }

        .container {
            width: 80%;
            margin: auto;
            overflow: hidden;
        }

        .header {
            background: #1e73be;
            padding: 10px 0;
            color: white;
        }

        .main {
            padding: 20px 0;
        }

        .content {
            background: white;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        .button {
            display: inline-block;
            padding: 10px 20px;
            text-decoration: none;
            color: white;
            background-color: #FFA500;
            border-radius: 5px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>Spedire</h1>
    </div>
    <div class="main">
        <div class="content">
            <h2>Hello {name},</h2>
            <h3>Reset your Spedire account password</h3>
            <h3>We received a request to reset your Spedire account password. To proceed, please follow the steps below:</h3>
            <h3>Click button below to reset your password and follow the on-screen instructions to create a new password for your account.</h3>

            <button>{link}</button>

            <p>Link is valid for 2 hours, after which you will need to request another password reset.</p>

            <p>If you didn't initiate this action or if you think you received this email by mistake, please contact support@spedireaccounts.com</p>

            <p>Regards</p>
            <p>Spedire Team</p> <br><br>

            <p>©  Powered by PolyThread</p>
        </div>
    </div>
</div>
</body>
</html>
