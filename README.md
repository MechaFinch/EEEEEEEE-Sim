# EEEEEEEE-Sim
 A simulator for the E8 Architecture I created, with a specification-type document [here](https://docs.google.com/document/d/1ZuOjC9-vyo861tgI32147iGDFwaUJDAP9w7fYyiy7tY/edit?usp=sharing)

In short, E8 is a small ISA with 16 bit fixed-length instructions and 4 8-bit registers. It was originally designed to be what would be needed to have a suitably functional but simple computer in Minecraft, and I'm working with it elsewhere, such as here, in its full capacity in terms of amount of RAM and ROM

When completed, the simulator will have a GUI which will include a hex view of RAM, hex & binary views of registers, IP, and current instruction, and a full breakdown of the current isntruction, including what locations are affected. Programs may be read from binary files of a to-be-specified format and asm files to be defined as well. Further, it will be able to step through the program or run at variable speeds.

Future/unsure features include saving VM state to a file and loading those.
