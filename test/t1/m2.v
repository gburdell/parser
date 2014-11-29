`include "f1.vh"

module m2(input [`N-1:0] a);
	output z;
	assign z = ~a;
endmodule
