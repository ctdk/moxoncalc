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

COPYRIGHT, LICENSE
==================

Copyright (c) 2017, Jeremy Bingham (<jeremy@goiardi.gl>)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
