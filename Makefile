JAVAC=javac
JAR=jar
sources = src/*.java
classes = $(sources:.java=.class)
OUTDIR=./lib

all: $(classes)

dir: $(OUTDIR)
	mkdir -p $(OUTDIR)

clean :
	$(RM) $OUTDIR/*.class
