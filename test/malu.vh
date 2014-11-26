`ifndef _malu_vh_
`define _malu_vh_

`define N 16
`define OPN 4

`define VLIWN ((3*`N)+(2*`OPN))

`define vliwSelA(_v) _v[`VLIWN-1:`VLIWN-`N]
`define vliwSelB(_v) _v[`VLIWN-`N-1:`VLIWN-(2*`N)]
`define vliwSelC(_v) _v[`VLIWN-(2*`N)-1:`VLIWN-(3*`N)]
`define vliwSelOp1(_v) _v[`VLIWN-(3*`N)-1:`VLIWN-(3*`N)-`OPN]
`define vliwSelOp2(_v) _v[`VLIWN-(3*`N)-`OPN-1:0]

`define ADD  4'd0
`define SUB  4'd1
`define AND  4'd2
`define OR   4'd3
`define XOR  4'd4
`define XNOR 4'd5
`define MULH 4'd6
`define MULL 4'd7
`define DIV  4'd8
`define MOD  4'd9
`define NOTA 4'd10
`define NOTB 4'd11

typedef struct {
	logic [`N-1:0] a,b,c;
	logic [`OPN-1:0] op1, op2;
} sAlu;

`endif 	//_malu_vh_

