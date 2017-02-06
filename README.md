moxoncalc
---------

DESCRIPTION
===========

A command-line calculator for Moxon antennas. To learn more about what a Moxon antenna *is*, see http://moxonantennaproject.com/.

USAGE
=====

```
Usage:
  java moxoncalc-<version>.jar [options]

Application options:
-f:	Frequency of antenna in megaHertz (Defaults to 14.0 MHz).
-d:	Diameter of wire (specify unit with -w) (Defaults to 14).
-u:	Units to use to display output. Choices are 'meter' (or 'm'), 'mm' (for millimeters), 'feet' (or 'f'), 'inch' (or 'i'), and 'wl' (for wavelength). Defaults to 'meter'.
-w:	Wire size units. Choices are 'AWG' (or 'a', or 'awg'), 'mm' (for millimeters), 'inch' (or 'i'), and 'wl' (for wavelength). Defaults to 'AWG'.
-F:	Format for output. Choices are 'full' (includes diagram and the lengths of the antenna components), 'short' (only the lenghts of the components, along with the frequency and wire size), and 'json' (a JSON representation of 'short'). Defaults to 'full'.
-v:	Print version number.
-h:	Print this help message.
```

OUTPUT
======

See the SAMPLE_OUTPUT.txt file for sample outputs from the utility.

BUILDING
========

Assuming your Java installation is properly set up, run `make && make jar` in this directory.

INSPIRATION
===========

The online Moxon antenna calculators at http://tippete.net/cgi-bin/moxgen.pl and http://ab1jx.1apps.com/ham/calcs/moxon/. Also, as a project to get familiarized with Java.

BUGS
====

Presumably. This is a learning project for myself, so at the very least there's probably some style issues I'm not aware of yet.
