module m1 (input [`N-1:0] a, b);
	output z;
	assign z = a & b;
	m2 u1();
endmodule
