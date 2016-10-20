docker_build:
		docker build -t inet/housingmodel:0.1 .

docker_run:
		docker run inet/housingmodel:0.1

ping:
		sandplatform --hostname $(SAND_HOSTNAME) ping

run:
		sandplatform run model.yaml

upload:
		sandplatform --hostname $(SAND_HOSTNAME) upload model model.yaml

project:
		sandplatform --hostname $(SAND_HOSTNAME) create project project.yaml
