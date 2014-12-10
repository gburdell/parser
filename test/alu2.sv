`timescale 1 ns / 1 ns

`include "malu.vh"

module alu(input clk, 
            input [`N-1:0] a,b,c, 
			input [`OPN-1:0] op1,op2,
			output reg [`N-1:0] z);
`include "alufn.vh"
	reg [`N-1:0] q, aq,bq,cq,cq2;
	reg [`OPN-1:0] op1q, op2q, op2q2;
	always @(posedge clk) begin
		{aq,bq,cq,cq2} <= {a,b,c,cq};
		{op1q,op2q,op2q2} <= {op1,op2,op2q};
		q <= alu(op1q,aq,bq);
		z <= alu(op2q2,q,cq2);
	end
endmodule

module alu2(input clk, 
            input [`N-1:0] a_0,a_1,b_0,b_1,c_0,c_1,
			input [`OPN-1:0] op1_0,op1_1,op2_0,op2_1,
			output reg [`N-1:0] z_0,z_1);
	alu 
		u0(clk,a_0,b_0,c_0,op1_0,op2_0,z_0),
		u1(clk,a_1,b_1,c_1,op1_1,op2_1,z_1);
endmodule

module alu2V2(input clk, clk_2x, rst,
            input [`N-1:0] a_0,a_1,b_0,b_1,c_0,c_1,
			input [`OPN-1:0] op1_0,op1_1,op2_0,op2_1,
			output reg [`N-1:0] z_0,z_1);
`include "alufn.vh"
    reg [`N-1:0] a_0q,a_1q,b_0q,b_1q,c_0q,c_1q,c_0q2,c_1q2;
    reg [`N-1:0] q_0q, q_1q; //1st stage
	reg [`OPN-1:0] op1_0q,op1_1q,op2_0q,op2_1q,op2_0q2,op2_1q2;
	always @(posedge clk) begin
		{a_0q,b_0q,c_0q,c_0q2} <= {a_0,b_0,c_0,c_0q};
		{op1_0q,op2_0q,op2_0q2} <= {op1_0,op2_0,op2_0q};
		{a_1q,b_1q,c_1q,c_1q2} <= {a_1,b_1,c_1,c_1q};
		{op1_1q,op2_1q,op2_1q2} <= {op1_1,op2_1,op2_1q};
	end
	reg sel;
	always @(posedge clk_2x) begin: b0
		reg [`N-1:0] a,b,z;
		reg [`N-1:0] op;
		sel <= (rst) ? 0 : ~sel;
		{op,a,b} = (sel) ? {op1_1q,a_1q,b_1q} : {op1_0q,a_0q,b_0q};
		z = alu(op,a,b);
		if (sel) q_1q <= z;
		else     q_0q <= z;
		{op,a,b} = (sel) ? {op2_1q2,q_1q,c_1q2} : {op2_0q2,q_0q,c_0q2};
		z = alu(op,a,b);
		if (sel) z_1 <= z;
		else     z_0 <= z;
	end
endmodule

/*
module alu2x4
		   #(parameter M = 4)
           (input clk, 
            input [`N-1:0] a[M-1:0][1:0],b[M-1:0][1:0],c[M-1:0][1:0], 
			input [`OPN-1:0] op1[M-1:0][1:0],op2[M-1:0][1:0],
			output reg [`N-1:0] z[M-1:0][1:0]);
	alu2 
		u0(clk,a[0],b[0],c[0],op1[0],op2[0],z[0]),
		u1(clk,a[1],b[1],c[1],op1[1],op2[1],z[1]),
		u2(clk,a[2],b[2],c[2],op1[2],op2[2],z[2]),
		u3(clk,a[3],b[3],c[3],op1[3],op2[3],z[3]);
endmodule
*/

`ifdef t_alu
module t_alu;
	parameter HALFT = 5;
	parameter M = 4;

	reg clk,clk_2x, rst;
    reg [`N-1:0] a_0,a_1,b_0,b_1,c_0,c_1;
	reg [`OPN-1:0] op1_0,op1_1,op2_0,op2_1;
	wire [`N-1:0] z_0,z_1,zV2_0,zV2_1;

	function [`N-1:0] randData;
		randData = $random % (1<<`N);
	endfunction

	function [`OPN-1:0] randOpcode;
		randOpcode = $random % (1<<`OPN);
	endfunction

	always @(clk)    clk    <= #(2*HALFT) ~clk;
	always @(clk_2x) clk_2x <= #HALFT ~clk_2x;

	initial begin: b0
		{clk_2x,clk,rst} = 3'b101;
		#(4*HALFT);
		rst = 0;
		#1;
`ifndef NO_DUMP
		$dumpfile("alu2.vcd");
		$dumpvars;//(0, dut);
`endif
		repeat (256) begin
			#(4 * HALFT);
			a_0 = randData();
			a_1 = randData();
			b_0 = randData();
			b_1 = randData();
			c_0 = randData();
			c_1 = randData();
			op1_0 = randOpcode();
			op1_1 = randOpcode();
			op2_0 = randOpcode();
			op2_1 = randOpcode();
		end
`ifndef NO_DUMP
		$dumpflush;
`endif
		$finish;
	end

//`ifdef NO_DUMP
	initial
		$monitor($time,,,"z0=%h z1=%h z0V2=%h z1V2=%h",z_0,z_1,zV2_0,zV2_1);
//`endif

	alu2   dut   (.*);
	alu2V2 dutV2 (.z_0(zV2_0),.z_1(zV2_1), .*);
endmodule
`endif

//`undef N
`undef NOT_DEFINED

`define N 64
module xxx;
	input [`N:0] a;
endmodule
