# EEEEEEEE-Sim
 A simulator for the E8 Architecture I created, with a specification-type document [here](https://docs.google.com/document/d/1ZuOjC9-vyo861tgI32147iGDFwaUJDAP9w7fYyiy7tY/edit?usp=sharing)
 
 If you're looking through the code and are bothered by some of the indentation, it uses tab characters corresponding to 4 sapces and github displays them with more spaces

In short, E8 is a small ISA with 16 bit fixed-length instructions and 4 8-bit registers. It was originally designed to be what would be needed to have a suitably functional but simple computer in Minecraft, and I'm working with it elsewhere, such as here, in its full capacity in terms of amount of RAM and ROM

# Current State
Currently, this project contains the high-level Simulator and the Assembler. The Simualtor is implemented to allow for variable data & address size, while the Assembler is unaffected by them, and both are implemented with the main set and the Stack and Multiply extensions as specified in the ISA
Currently, I am working on writing the Pipelined Simualtor, which is a Simulator that allows the pipelining of the simulated processor for use in my computer science "study" project exploring the effects of pipelining on performance.
