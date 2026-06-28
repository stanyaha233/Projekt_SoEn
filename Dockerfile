FROM hseeberger/scala-sbt:17.0.2_1.6.2_2.13.8
WORKDIR /Projekt_Uno
ADD . /Projekt_Uno
ENV GUI_MODE="false"
CMD ["sbt", "run"]

