from flask import Flask, request, jsonify
import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
def send_email():
    # Parse request data
    
    
    recipient_email = 'vicktonycruz@gmail.com'
    subject = 'Test Email'
    body = 'Test Email'
    print(recipient_email)
    print(subject)
    print(body)

    # Set up the SMTP server
    smtp_server = 'smtp.gmail.com'
    smtp_port = 587

    sender_email = 'abinandhan1934596@gmail.com'
    sender_password = 'rqrc liey ezya texp'

    # Create a MIME multipart message
    message = MIMEMultipart()
    message['From'] = sender_email
    message['To'] = recipient_email
    message['Subject'] = subject

    # Attach the body of the email
    message.attach(MIMEText(body, 'plain'))

    # Connect to the SMTP server
    server = smtplib.SMTP(smtp_server, smtp_port)
    server.starttls()

    # Log in to the SMTP server
    server.login(sender_email, sender_password)

    # Send the email
    server.sendmail(sender_email, recipient_email, message.as_string())

    # Quit the SMTP server
    server.quit()

    return jsonify({'message': 'Email sent successfully'})

if __name__ == '__main__':
    send_email()

