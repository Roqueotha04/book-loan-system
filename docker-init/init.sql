CREATE DATABASE book_loan_system;
GO

USE book_loan_system;
GO

CREATE LOGIN sa_user WITH PASSWORD = 'Bddroque123!';
CREATE USER sa_user FOR LOGIN sa_user;
ALTER ROLE db_owner ADD MEMBER sa_user;
GO