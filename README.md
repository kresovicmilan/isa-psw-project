# ISA-PS-project Tim17

Web application for clinical center. The goal of this application is to make administrating clinics as well as scheduling appointments fast and easy for all users, namely, patients, medical staff and admins

## Download instructions

These commands will get you a copy of project for dev and testing purposes
```
$ git clone https://github.com/marko-13/isa-ps-project.git
```

## Prerequisites

To successfully run the application on your local machine please install following software

* Java SDK v1.8 [*(download link)*](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* Apache Maven v3.6.3 [*(download link)*](https://maven.apache.org/download.cgi) (or use IDE of your choice, such as IntelliJ Idea, Eclipse etc.)
* NodeJS v12.13.0 [*(download link)*](https://nodejs.org/en/blog/release/v12.13.0/)
* PostgreSQL v12.0 [*(download link)*](https://www.postgresql.org/download/)

## Configuration

Run pgAdmin and create database named *isa-project*, set password to *krokodil* and set username to *postgres*

Open cmd or terminal and navigate to medical-clinic-frontend folder. When opend type *npm install* to install all libraries

## Starting

### Backend

1) If you are using Apache Maven, go to the medicalClinic directory inside the application's root directory, open cmd or terminal and type: *mvn spring-boot:run*

2) In case you are using ide, just open/import the medicalClinic directory in it and run medicalClinicApplication file.

### Frontend

Open cmd or terminal and navigate to medical-clinic-frontend folder. When opend type *npm start* and application should start and open new window in browser

## Deployment

*Note* 
Because of the free Heroku version when accessing the website please wait approximately 15 seconds after the first access and then refresh the page for servers to wake up.

If there is no data in database, run the following command:
pg:psql --app floating-lowlands-68738 < data-postgres.sql

[Heroku](https://peaceful-wave-53119.herokuapp.com/)

## Authors
- Student 1: Marko Jovanovic 
- Student 2: Boris Zavis
- Student 3: Milan Kresovic
