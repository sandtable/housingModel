FROM openjdk:8-jdk-alpine

ADD modelsrc/data/AgeMarginalPDFstatic.csv /opt/
ADD modelsrc/data/IncomeGivenAge.csv /opt/

ADD housingModel_ST.jar /opt/

RUN mkdir /mnt/data

WORKDIR /opt

CMD java -jar housingModel_ST.jar
