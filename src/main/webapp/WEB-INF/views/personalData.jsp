<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.dropbox.core.DbxEntry"%>
<%@page import="java.util.List"%>
<% List<DbxEntry> filesInRoot = (List<DbxEntry>) request.getAttribute("filesInRoot"); %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>IAM-Prototype</title>
        <!-- Latest compiled and minified CSS -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
        <!-- Latest compiled and minified JavaScript -->
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
        <style type="text/css">

            .margin{
                margin-left: 3%; 
            }

            .oauth{
                width: 75%;
            }
            
            .others {
                width : 30%;
            }
        </style>
    </head>
    <body>
        <br />
        <h2 class="margin">Oauth client IAM-Protype</h2>
        <br />
        <h4 class="margin">Oauth Specific Information</h4>


        <div class="margin">
            <table class="table table-bordered oauth">
                <tr class="active">
                    <th>State</th>
                    <th>Code</th>
                    <th>Access token</th>
                </tr>
                <tr>
                    <td>${state}</td>
                    <td>${code}</td>    
                    <td>${accessToken}</td> 
                </tr>
            </table>

            <br>
            <h4>User Information</h4>
            <table class="table table-bordered others">
                <tr class="active">
                    <th>Name</th>
                    <th>country</th>
                </tr>
                <tr>
                    <td>${userName}</td>    
                    <td>${country}</td> 
                </tr>
            </table>
            <br>
            <h4>Uploaded file</h4>
            <table class="table table-bordered others">
                <tr class="active">
                    <th>Name</th>
                    <th>Size</th>
                </tr>
                <tr>
                    <td>${uploadedFile}</td>    
                    <td>${uploadedFileSize}</td> 
                </tr>
            </table>
            <br>
            <h4>Files in the root path</h4>
            <table class="table table-bordered others">
                <tr class="active">
                    <th>Name</th>
                    <th>Is Folder</th>
                </tr>
                <% for (DbxEntry file : filesInRoot){ %>
                <tr>
                    <td><%=file.name%></td>    
                    <td><%=file.isFolder()%></td>
                </tr>
                <% } %>
            </table>
        </div>
    </body>
</html>