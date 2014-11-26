
	function [`N-1:0] alu(input [`OPN-1:0] opcode, input [`N-1:0] a,b);
		reg	[(`N*2)-1:0] prod;
		case (opcode)
			`ADD : alu = {a} + {b};
			`SUB : alu = {a} - {b};
			`AND : alu = {a} & {b};
			`OR  : alu = {a} | {b};
			`XOR : alu = {a} ^ {b};
			`XNOR : alu = {a} ~^ {b};
			`MULH : begin: b0
				prod = {a} * {b};
				alu = prod[(`N*2)-1:`N];
			end
			`MULL : begin: b1
				prod = {a} * {b};
				alu = prod[`N-1:0];
			end
`ifndef NO_DIV
			`DIV : alu = {a} / {b};
			`MOD : alu = {a} % {b};
`endif
			`NOTA : alu = ~a;
			`NOTB : alu = ~b;
			default : alu = {a};
		endcase
	endfunction

