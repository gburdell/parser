`include "m1.vh"

`define CAT(dog) /``/ i_am_a_``dog and you_are_also_a_``dog and not a fish 
`define DOG(cat) /``/ how about `CAT(cat)
`define EEK(bat) here_is```CAT(bat) and a dog
`define PASTE(that) this``that

module m1; 
    `FOO(cat)
	`CAT(mouse)
	`DOG(lion)
	`EEK(wolf) and more
	`PASTE(_and_more)
endmodule
