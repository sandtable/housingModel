FROM openjdk:8-jdk-alpine

ADD config.properties /opt/
ADD modelsrc/data/AgeMarginalPDFstatic.csv /opt/
ADD modelsrc/data/IncomeGivenAge.csv /opt/

ADD housingModel_ST.jar /opt/

WORKDIR /opt

CMD java -jar housingModel_ST.jar
