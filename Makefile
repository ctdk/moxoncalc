JAVAC=javac
JAR=jar
sources = $(wildcard src/*.java)
classes = $(sources:.java=.class)
OUTDIR=./lib
JAVACFLAGS=-d $(OUTDIR)
MCVERSION=0.1.0

all: dir $(classes)

dir: 
	mkdir -p $(OUTDIR)

clean :
	rm -r $(OUTDIR)

%.class : %.java
	$(JAVAC) $(JAVACFLAGS) $<

jar:
	$(JAR) cfm moxoncalc-$(MCVERSION).jar Manifest.txt -C $(OUTDIR) .
