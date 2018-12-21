/*
 Copyright (c) 2012 Gildas Lormeau. All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice,
 this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in
 the documentation and/or other materials provided with the distribution.

 3. The names of the authors may not be used to endorse or promote products
 derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JCRAFT,
 INC. OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * This program is based on JZlib 1.0.2 ymnk, JCraft,Inc.
 * JZlib is based on zlib-1.1.3, so all credit should go authors
 * Jean-loup Gailly(jloup@gzip.org) and Mark Adler(madler@alumni.caltech.edu)
 * and contributors of zlib.
 */

(function(obj) {

    // Global
    var MAX_BITS = 15;

    var Z_OK = 0;
    var Z_STREAM_END = 1;
    var Z_NEED_DICT = 2;
    var Z_STREAM_ERROR = -2;
    var Z_DATA_ERROR = -3;
    var Z_MEM_ERROR = -4;
    var Z_BUF_ERROR = -5;

    var inflate_mask = [ 0x00000000, 0x00000001, 0x00000003, 0x00000007, 0x0000000f, 0x0000001f, 0x0000003f, 0x0000007f, 0x000000ff, 0x000001ff, 0x000003ff,
        0x000007ff, 0x00000fff, 0x00001fff, 0x00003fff, 0x00007fff, 0x0000ffff ];

    var MANY = 1440;

    // JZlib version : "1.0.2"
    var Z_NO_FLUSH = 0;
    var Z_FINISH = 4;

    // InfTree
    var fixed_bl = 9;
    var fixed_bd = 5;

    var fixed_tl = [ 96, 7, 256, 0, 8, 80, 0, 8, 16, 84, 8, 115, 82, 7, 31, 0, 8, 112, 0, 8, 48, 0, 9, 192, 80, 7, 10, 0, 8, 96, 0, 8, 32, 0, 9, 160, 0, 8, 0,
        0, 8, 128, 0, 8, 64, 0, 9, 224, 80, 7, 6, 0, 8, 88, 0, 8, 24, 0, 9, 144, 83, 7, 59, 0, 8, 120, 0, 8, 56, 0, 9, 208, 81, 7, 17, 0, 8, 104, 0, 8, 40,
        0, 9, 176, 0, 8, 8, 0, 8, 136, 0, 8, 72, 0, 9, 240, 80, 7, 4, 0, 8, 84, 0, 8, 20, 85, 8, 227, 83, 7, 43, 0, 8, 116, 0, 8, 52, 0, 9, 200, 81, 7, 13,
        0, 8, 100, 0, 8, 36, 0, 9, 168, 0, 8, 4, 0, 8, 132, 0, 8, 68, 0, 9, 232, 80, 7, 8, 0, 8, 92, 0, 8, 28, 0, 9, 152, 84, 7, 83, 0, 8, 124, 0, 8, 60,
        0, 9, 216, 82, 7, 23, 0, 8, 108, 0, 8, 44, 0, 9, 184, 0, 8, 12, 0, 8, 140, 0, 8, 76, 0, 9, 248, 80, 7, 3, 0, 8, 82, 0, 8, 18, 85, 8, 163, 83, 7,
        35, 0, 8, 114, 0, 8, 50, 0, 9, 196, 81, 7, 11, 0, 8, 98, 0, 8, 34, 0, 9, 164, 0, 8, 2, 0, 8, 130, 0, 8, 66, 0, 9, 228, 80, 7, 7, 0, 8, 90, 0, 8,
        26, 0, 9, 148, 84, 7, 67, 0, 8, 122, 0, 8, 58, 0, 9, 212, 82, 7, 19, 0, 8, 106, 0, 8, 42, 0, 9, 180, 0, 8, 10, 0, 8, 138, 0, 8, 74, 0, 9, 244, 80,
        7, 5, 0, 8, 86, 0, 8, 22, 192, 8, 0, 83, 7, 51, 0, 8, 118, 0, 8, 54, 0, 9, 204, 81, 7, 15, 0, 8, 102, 0, 8, 38, 0, 9, 172, 0, 8, 6, 0, 8, 134, 0,
        8, 70, 0, 9, 236, 80, 7, 9, 0, 8, 94, 0, 8, 30, 0, 9, 156, 84, 7, 99, 0, 8, 126, 0, 8, 62, 0, 9, 220, 82, 7, 27, 0, 8, 110, 0, 8, 46, 0, 9, 188, 0,
        8, 14, 0, 8, 142, 0, 8, 78, 0, 9, 252, 96, 7, 256, 0, 8, 81, 0, 8, 17, 85, 8, 131, 82, 7, 31, 0, 8, 113, 0, 8, 49, 0, 9, 194, 80, 7, 10, 0, 8, 97,
        0, 8, 33, 0, 9, 162, 0, 8, 1, 0, 8, 129, 0, 8, 65, 0, 9, 226, 80, 7, 6, 0, 8, 89, 0, 8, 25, 0, 9, 146, 83, 7, 59, 0, 8, 121, 0, 8, 57, 0, 9, 210,
        81, 7, 17, 0, 8, 105, 0, 8, 41, 0, 9, 178, 0, 8, 9, 0, 8, 137, 0, 8, 73, 0, 9, 242, 80, 7, 4, 0, 8, 85, 0, 8, 21, 80, 8, 258, 83, 7, 43, 0, 8, 117,
        0, 8, 53, 0, 9, 202, 81, 7, 13, 0, 8, 101, 0, 8, 37, 0, 9, 170, 0, 8, 5, 0, 8, 133, 0, 8, 69, 0, 9, 234, 80, 7, 8, 0, 8, 93, 0, 8, 29, 0, 9, 154,
        84, 7, 83, 0, 8, 125, 0, 8, 61, 0, 9, 218, 82, 7, 23, 0, 8, 109, 0, 8, 45, 0, 9, 186, 0, 8, 13, 0, 8, 141, 0, 8, 77, 0, 9, 250, 80, 7, 3, 0, 8, 83,
        0, 8, 19, 85, 8, 195, 83, 7, 35, 0, 8, 115, 0, 8, 51, 0, 9, 198, 81, 7, 11, 0, 8, 99, 0, 8, 35, 0, 9, 166, 0, 8, 3, 0, 8, 131, 0, 8, 67, 0, 9, 230,
        80, 7, 7, 0, 8, 91, 0, 8, 27, 0, 9, 150, 84, 7, 67, 0, 8, 123, 0, 8, 59, 0, 9, 214, 82, 7, 19, 0, 8, 107, 0, 8, 43, 0, 9, 182, 0, 8, 11, 0, 8, 139,
        0, 8, 75, 0, 9, 246, 80, 7, 5, 0, 8, 87, 0, 8, 23, 192, 8, 0, 83, 7, 51, 0, 8, 119, 0, 8, 55, 0, 9, 206, 81, 7, 15, 0, 8, 103, 0, 8, 39, 0, 9, 174,
        0, 8, 7, 0, 8, 135, 0, 8, 71, 0, 9, 238, 80, 7, 9, 0, 8, 95, 0, 8, 31, 0, 9, 158, 84, 7, 99, 0, 8, 127, 0, 8, 63, 0, 9, 222, 82, 7, 27, 0, 8, 111,
        0, 8, 47, 0, 9, 190, 0, 8, 15, 0, 8, 143, 0, 8, 79, 0, 9, 254, 96, 7, 256, 0, 8, 80, 0, 8, 16, 84, 8, 115, 82, 7, 31, 0, 8, 112, 0, 8, 48, 0, 9,
        193, 80, 7, 10, 0, 8, 96, 0, 8, 32, 0, 9, 161, 0, 8, 0, 0, 8, 128, 0, 8, 64, 0, 9, 225, 80, 7, 6, 0, 8, 88, 0, 8, 24, 0, 9, 145, 83, 7, 59, 0, 8,
        120, 0, 8, 56, 0, 9, 209, 81, 7, 17, 0, 8, 104, 0, 8, 40, 0, 9, 177, 0, 8, 8, 0, 8, 136, 0, 8, 72, 0, 9, 241, 80, 7, 4, 0, 8, 84, 0, 8, 20, 85, 8,
        227, 83, 7, 43, 0, 8, 116, 0, 8, 52, 0, 9, 201, 81, 7, 13, 0, 8, 100, 0, 8, 36, 0, 9, 169, 0, 8, 4, 0, 8, 132, 0, 8, 68, 0, 9, 233, 80, 7, 8, 0, 8,
        92, 0, 8, 28, 0, 9, 153, 84, 7, 83, 0, 8, 124, 0, 8, 60, 0, 9, 217, 82, 7, 23, 0, 8, 108, 0, 8, 44, 0, 9, 185, 0, 8, 12, 0, 8, 140, 0, 8, 76, 0, 9,
        249, 80, 7, 3, 0, 8, 82, 0, 8, 18, 85, 8, 163, 83, 7, 35, 0, 8, 114, 0, 8, 50, 0, 9, 197, 81, 7, 11, 0, 8, 98, 0, 8, 34, 0, 9, 165, 0, 8, 2, 0, 8,
        130, 0, 8, 66, 0, 9, 229, 80, 7, 7, 0, 8, 90, 0, 8, 26, 0, 9, 149, 84, 7, 67, 0, 8, 122, 0, 8, 58, 0, 9, 213, 82, 7, 19, 0, 8, 106, 0, 8, 42, 0, 9,
        181, 0, 8, 10, 0, 8, 138, 0, 8, 74, 0, 9, 245, 80, 7, 5, 0, 8, 86, 0, 8, 22, 192, 8, 0, 83, 7, 51, 0, 8, 118, 0, 8, 54, 0, 9, 205, 81, 7, 15, 0, 8,
        102, 0, 8, 38, 0, 9, 173, 0, 8, 6, 0, 8, 134, 0, 8, 70, 0, 9, 237, 80, 7, 9, 0, 8, 94, 0, 8, 30, 0, 9, 157, 84, 7, 99, 0, 8, 126, 0, 8, 62, 0, 9,
        221, 82, 7, 27, 0, 8, 110, 0, 8, 46, 0, 9, 189, 0, 8, 14, 0, 8, 142, 0, 8, 78, 0, 9, 253, 96, 7, 256, 0, 8, 81, 0, 8, 17, 85, 8, 131, 82, 7, 31, 0,
        8, 113, 0, 8, 49, 0, 9, 195, 80, 7, 10, 0, 8, 97, 0, 8, 33, 0, 9, 163, 0, 8, 1, 0, 8, 129, 0, 8, 65, 0, 9, 227, 80, 7, 6, 0, 8, 89, 0, 8, 25, 0, 9,
        147, 83, 7, 59, 0, 8, 121, 0, 8, 57, 0, 9, 211, 81, 7, 17, 0, 8, 105, 0, 8, 41, 0, 9, 179, 0, 8, 9, 0, 8, 137, 0, 8, 73, 0, 9, 243, 80, 7, 4, 0, 8,
        85, 0, 8, 21, 80, 8, 258, 83, 7, 43, 0, 8, 117, 0, 8, 53, 0, 9, 203, 81, 7, 13, 0, 8, 101, 0, 8, 37, 0, 9, 171, 0, 8, 5, 0, 8, 133, 0, 8, 69, 0, 9,
        235, 80, 7, 8, 0, 8, 93, 0, 8, 29, 0, 9, 155, 84, 7, 83, 0, 8, 125, 0, 8, 61, 0, 9, 219, 82, 7, 23, 0, 8, 109, 0, 8, 45, 0, 9, 187, 0, 8, 13, 0, 8,
        141, 0, 8, 77, 0, 9, 251, 80, 7, 3, 0, 8, 83, 0, 8, 19, 85, 8, 195, 83, 7, 35, 0, 8, 115, 0, 8, 51, 0, 9, 199, 81, 7, 11, 0, 8, 99, 0, 8, 35, 0, 9,
        167, 0, 8, 3, 0, 8, 131, 0, 8, 67, 0, 9, 231, 80, 7, 7, 0, 8, 91, 0, 8, 27, 0, 9, 151, 84, 7, 67, 0, 8, 123, 0, 8, 59, 0, 9, 215, 82, 7, 19, 0, 8,
        107, 0, 8, 43, 0, 9, 183, 0, 8, 11, 0, 8, 139, 0, 8, 75, 0, 9, 247, 80, 7, 5, 0, 8, 87, 0, 8, 23, 192, 8, 0, 83, 7, 51, 0, 8, 119, 0, 8, 55, 0, 9,
        207, 81, 7, 15, 0, 8, 103, 0, 8, 39, 0, 9, 175, 0, 8, 7, 0, 8, 135, 0, 8, 71, 0, 9, 239, 80, 7, 9, 0, 8, 95, 0, 8, 31, 0, 9, 159, 84, 7, 99, 0, 8,
        127, 0, 8, 63, 0, 9, 223, 82, 7, 27, 0, 8, 111, 0, 8, 47, 0, 9, 191, 0, 8, 15, 0, 8, 143, 0, 8, 79, 0, 9, 255 ];
    var fixed_td = [ 80, 5, 1, 87, 5, 257, 83, 5, 17, 91, 5, 4097, 81, 5, 5, 89, 5, 1025, 85, 5, 65, 93, 5, 16385, 80, 5, 3, 88, 5, 513, 84, 5, 33, 92, 5,
        8193, 82, 5, 9, 90, 5, 2049, 86, 5, 129, 192, 5, 24577, 80, 5, 2, 87, 5, 385, 83, 5, 25, 91, 5, 6145, 81, 5, 7, 89, 5, 1537, 85, 5, 97, 93, 5,
        24577, 80, 5, 4, 88, 5, 769, 84, 5, 49, 92, 5, 12289, 82, 5, 13, 90, 5, 3073, 86, 5, 193, 192, 5, 24577 ];

    // Tables for deflate from PKZIP's appnote.txt.
    var cplens = [ // Copy lengths for literal codes 257..285
        3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 17, 19, 23, 27, 31, 35, 43, 51, 59, 67, 83, 99, 115, 131, 163, 195, 227, 258, 0, 0 ];

    // see note #13 above about 258
    var cplext = [ // Extra bits for literal codes 257..285
        0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 0, 112, 112 // 112==invalid
    ];

    var cpdist = [ // Copy offsets for distance codes 0..29
        1, 2, 3, 4, 5, 7, 9, 13, 17, 25, 33, 49, 65, 97, 129, 193, 257, 385, 513, 769, 1025, 1537, 2049, 3073, 4097, 6145, 8193, 12289, 16385, 24577 ];

    var cpdext = [ // Extra bits for distance codes
        0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13 ];

    // If BMAX needs to be larger than 16, then h and x[] should be uLong.
    var BMAX = 15; // maximum bit length of any code

    function InfTree() {
        var that = this;

        var hn; // hufts used in space
        var v; // work area for huft_build
        var c; // bit length count table
        var r; // table entry for structure assignment
        var u; // table stack
        var x; // bit offsets, then code stack

        function huft_build(b, // code lengths in bits (all assumed <=
                            // BMAX)
                            bindex, n, // number of codes (assumed <= 288)
                            s, // number of simple-valued codes (0..s-1)
                            d, // list of base values for non-simple codes
                            e, // list of extra bits for non-simple codes
                            t, // result: starting table
                            m, // maximum lookup bits, returns actual
                            hp,// space for trees
                            hn,// hufts used in space
                            v // working area: values in order of bit length
        ) {
            // Given a list of code lengths and a maximum table size, make a set of
            // tables to decode that set of codes. Return Z_OK on success,
            // Z_BUF_ERROR
            // if the given code set is incomplete (the tables are still built in
            // this
            // case), Z_DATA_ERROR if the input is invalid (an over-subscribed set
            // of
            // lengths), or Z_MEM_ERROR if not enough memory.

            var a; // counter for codes of length k
            var f; // i repeats in table every f entries
            var g; // maximum code length
            var h; // table level
            var i; // counter, current code
            var j; // counter
            var k; // number of bits in current code
            var l; // bits per table (returned in m)
            var mask; // (1 << w) - 1, to avoid cc -O bug on HP
            var p; // pointer into c[], b[], or v[]
            var q; // points to current table
            var w; // bits before this table == (l * h)
            var xp; // pointer into x
            var y; // number of dummy codes added
            var z; // number of entries in current table

            // Generate counts for each bit length

            p = 0;
            i = n;
            do {
                c[b[bindex + p]]++;
                p++;
                i--; // assume all entries <= BMAX
            } while (i !== 0);

            if (c[0] == n) { // null input--all zero length codes
                t[0] = -1;
                m[0] = 0;
                return Z_OK;
            }

            // Find minimum and maximum length, bound *m by those
            l = m[0];
            for (j = 1; j <= BMAX; j++)
                if (c[j] !== 0)
                    break;
            k = j; // minimum code length
            if (l < j) {
                l = j;
            }
            for (i = BMAX; i !== 0; i--) {
                if (c[i] !== 0)
                    break;
            }
            g = i; // maximum code length
            if (l > i) {
                l = i;
            }
            m[0] = l;

            // Adjust last length count to fill out codes, if needed
            for (y = 1 << j; j < i; j++, y <<= 1) {
                if ((y -= c[j]) < 0) {
                    return Z_DATA_ERROR;
                }
            }
            if ((y -= c[i]) < 0) {
                return Z_DATA_ERROR;
            }
            c[i] += y;

            // Generate starting offsets into the value table for each length
            x[1] = j = 0;
            p = 1;
            xp = 2;
            while (--i !== 0) { // note that i == g from above
                x[xp] = (j += c[p]);
                xp++;
                p++;
            }

            // Make a table of values in order of bit lengths
            i = 0;
            p = 0;
            do {
                if ((j = b[bindex + p]) !== 0) {
                    v[x[j]++] = i;
                }
                p++;
            } while (++i < n);
            n = x[g]; // set n to length of v

            // Generate the Huffman codes and for each, make the table entries
            x[0] = i = 0; // first Huffman code is zero
            p = 0; // grab values in bit order
            h = -1; // no tables yet--level -1
            w = -l; // bits decoded == (l * h)
            u[0] = 0; // just to keep compilers happy
            q = 0; // ditto
            z = 0; // ditto

            // go through the bit lengths (k already is bits in shortest code)
            for (; k <= g; k++) {
                a = c[k];
                while (a-- !== 0) {
                    // here i is the Huffman code of length k bits for value *p
                    // make tables up to required level
                    while (k > w + l) {
                        h++;
                        w += l; // previous table always l bits
                        // compute minimum size table less than or equal to l bits
                        z = g - w;
                        z = (z > l) ? l : z; // table size upper limit
                        if ((f = 1 << (j = k - w)) > a + 1) { // try a k-w bit table
                            // too few codes for
                            // k-w bit table
                            f -= a + 1; // deduct codes from patterns left
                            xp = k;
                            if (j < z) {
                                while (++j < z) { // try smaller tables up to z bits
                                    if ((f <<= 1) <= c[++xp])
                                        break; // enough codes to use up j bits
                                    f -= c[xp]; // else deduct codes from patterns
                                }
                            }
                        }
                        z = 1 << j; // table entries for j-bit table

                        // allocate new table
                        if (hn[0] + z > MANY) { // (note: doesn't matter for fixed)
                            return Z_DATA_ERROR; // overflow of MANY
                        }
                        u[h] = q = /* hp+ */hn[0]; // DEBUG
                        hn[0] += z;

                        // connect to last table, if there is one
                        if (h !== 0) {
                            x[h] = i; // save pattern for backing up
                            r[0] = /* (byte) */j; // bits in this table
                            r[1] = /* (byte) */l; // bits to dump before this table
                            j = i >>> (w - l);
                            r[2] = /* (int) */(q - u[h - 1] - j); // offset to this table
                            hp.set(r, (u[h - 1] + j) * 3);
                            // to
                            // last
                            // table
                        } else {
                            t[0] = q; // first table is returned result
                        }
                    }

                    // set up table entry in r
                    r[1] = /* (byte) */(k - w);
                    if (p >= n) {
                        r[0] = 128 + 64; // out of values--invalid code
                    } else if (v[p] < s) {
                        r[0] = /* (byte) */(v[p] < 256 ? 0 : 32 + 64); // 256 is
                        // end-of-block
                        r[2] = v[p++]; // simple code is just the value
                    } else {
                        r[0] = /* (byte) */(e[v[p] - s] + 16 + 64); // non-simple--look
                        // up in lists
                        r[2] = d[v[p++] - s];
                    }

                    // fill code-like entries with r
                    f = 1 << (k - w);
                    for (j = i >>> w; j < z; j += f) {
                        hp.set(r, (q + j) * 3);
                    }

                    // backwards increment the k-bit code i
                    for (j = 1 << (k - 1); (i & j) !== 0; j >>>= 1) {
                        i ^= j;
                    }
                    i ^= j;

                    // backup over finished tables
                    mask = (1 << w) - 1; // needed on HP, cc -O bug
                    while ((i & mask) != x[h]) {
                        h--; // don't need to update q
                        w -= l;
                        mask = (1 << w) - 1;
                    }
                }
            }
            // Return Z_BUF_ERROR if we were given an incomplete table
            return y !== 0 && g != 1 ? Z_BUF_ERROR : Z_OK;
        }

        function initWorkArea(vsize) {
            var i;
            if (!hn) {
                hn = []; // []; //new Array(1);
                v = []; // new Array(vsize);
                c = new Int32Array(BMAX + 1); // new Array(BMAX + 1);
                r = []; // new Array(3);
                u = new Int32Array(BMAX); // new Array(BMAX);
                x = new Int32Array(BMAX + 1); // new Array(BMAX + 1);
            }
            if (v.length < vsize) {
                v = []; // new Array(vsize);
            }
            for (i = 0; i < vsize; i++) {
                v[i] = 0;
            }
            for (i = 0; i < BMAX + 1; i++) {
                c[i] = 0;
            }
            for (i = 0; i < 3; i++) {
                r[i] = 0;
            }
            // for(int i=0; i<BMAX; i++){u[i]=0;}
            u.set(c.subarray(0, BMAX), 0);
            // for(int i=0; i<BMAX+1; i++){x[i]=0;}
            x.set(c.subarray(0, BMAX + 1), 0);
        }

        that.inflate_trees_bits = function(c, // 19 code lengths
                                           bb, // bits tree desired/actual depth
                                           tb, // bits tree result
                                           hp, // space for trees
                                           z // for messages
        ) {
            var result;
            initWorkArea(19);
            hn[0] = 0;
            result = huft_build(c, 0, 19, 19, null, null, tb, bb, hp, hn, v);

            if (result == Z_DATA_ERROR) {
                z.msg = "oversubscribed dynamic bit lengths tree";
            } else if (result == Z_BUF_ERROR || bb[0] === 0) {
                z.msg = "incomplete dynamic bit lengths tree";
                result = Z_DATA_ERROR;
            }
            return result;
        };

        that.inflate_trees_dynamic = function(nl, // number of literal/length codes
                                              nd, // number of distance codes
                                              c, // that many (total) code lengths
                                              bl, // literal desired/actual bit depth
                                              bd, // distance desired/actual bit depth
                                              tl, // literal/length tree result
                                              td, // distance tree result
                                              hp, // space for trees
                                              z // for messages
        ) {
            var result;

            // build literal/length tree
            initWorkArea(288);
            hn[0] = 0;
            result = huft_build(c, 0, nl, 257, cplens, cplext, tl, bl, hp, hn, v);
            if (result != Z_OK || bl[0] === 0) {
                if (result == Z_DATA_ERROR) {
                    z.msg = "oversubscribed literal/length tree";
                } else if (result != Z_MEM_ERROR) {
                    z.msg = "incomplete literal/length tree";
                    result = Z_DATA_ERROR;
                }
                return result;
            }

            // build distance tree
            initWorkArea(288);
            result = huft_build(c, nl, nd, 0, cpdist, cpdext, td, bd, hp, hn, v);

            if (result != Z_OK || (bd[0] === 0 && nl > 257)) {
                if (result == Z_DATA_ERROR) {
                    z.msg = "oversubscribed distance tree";
                } else if (result == Z_BUF_ERROR) {
                    z.msg = "incomplete distance tree";
                    result = Z_DATA_ERROR;
                } else if (result != Z_MEM_ERROR) {
                    z.msg = "empty distance tree with lengths";
                    result = Z_DATA_ERROR;
                }
                return result;
            }

            return Z_OK;
        };

    }

    InfTree.inflate_trees_fixed = function(bl, // literal desired/actual bit depth
                                           bd, // distance desired/actual bit depth
                                           tl,// literal/length tree result
                                           td// distance tree result
    ) {
        bl[0] = fixed_bl;
        bd[0] = fixed_bd;
        tl[0] = fixed_tl;
        td[0] = fixed_td;
        return Z_OK;
    };

    // InfCodes

    // waiting for "i:"=input,
    // "o:"=output,
    // "x:"=nothing
    var START = 0; // x: set up for LEN
    var LEN = 1; // i: get length/literal/eob next
    var LENEXT = 2; // i: getting length extra (have base)
    var DIST = 3; // i: get distance next
    var DISTEXT = 4;// i: getting distance extra
    var COPY = 5; // o: copying bytes in window, waiting
    // for space
    var LIT = 6; // o: got literal, waiting for output
    // space
    var WASH = 7; // o: got eob, possibly still output
    // waiting
    var END = 8; // x: got eob and all data flushed
    var BADCODE = 9;// x: got error

    function InfCodes() {
        var that = this;

        var mode; // current inflate_codes mode

        // mode dependent information
        var len = 0;

        var tree; // pointer into tree
        var tree_index = 0;
        var need = 0; // bits needed

        var lit = 0;

        // if EXT or COPY, where and how much
        var get = 0; // bits to get for extra
        var dist = 0; // distance back to copy from

        var lbits = 0; // ltree bits decoded per branch
        var dbits = 0; // dtree bits decoder per branch
        var ltree; // literal/length/eob tree
        var ltree_index = 0; // literal/length/eob tree
        var dtree; // distance tree
        var dtree_index = 0; // distance tree

        // Called with number of bytes left to write in window at least 258
        // (the maximum string length) and number of input bytes available
        // at least ten. The ten bytes are six bytes for the longest length/
        // distance pair plus four bytes for overloading the bit buffer.

        function inflate_fast(bl, bd, tl, tl_index, td, td_index, s, z) {
            var t; // temporary pointer
            var tp; // temporary pointer
            var tp_index; // temporary pointer
            var e; // extra bits or operation
            var b; // bit buffer
            var k; // bits in bit buffer
            var p; // input data pointer
            var n; // bytes available there
            var q; // output window write pointer
            var m; // bytes to end of window or read pointer
            var ml; // mask for literal/length tree
            var md; // mask for distance tree
            var c; // bytes to copy
            var d; // distance back to copy from
            var r; // copy source pointer

            var tp_index_t_3; // (tp_index+t)*3

            // load input, output, bit values
            p = z.next_in_index;
            n = z.avail_in;
            b = s.bitb;
            k = s.bitk;
            q = s.write;
            m = q < s.read ? s.read - q - 1 : s.end - q;

            // initialize masks
            ml = inflate_mask[bl];
            md = inflate_mask[bd];

            // do until not enough input or output space for fast loop
            do { // assume called with m >= 258 && n >= 10
                // get literal/length code
                while (k < (20)) { // max bits for literal/length code
                    n--;
                    b |= (z.read_byte(p++) & 0xff) << k;
                    k += 8;
                }

                t = b & ml;
                tp = tl;
                tp_index = tl_index;
                tp_index_t_3 = (tp_index + t) * 3;
                if ((e = tp[tp_index_t_3]) === 0) {
                    b >>= (tp[tp_index_t_3 + 1]);
                    k -= (tp[tp_index_t_3 + 1]);

                    s.window[q++] = /* (byte) */tp[tp_index_t_3 + 2];
                    m--;
                    continue;
                }
                do {

                    b >>= (tp[tp_index_t_3 + 1]);
                    k -= (tp[tp_index_t_3 + 1]);

                    if ((e & 16) !== 0) {
                        e &= 15;
                        c = tp[tp_index_t_3 + 2] + (/* (int) */b & inflate_mask[e]);

                        b >>= e;
                        k -= e;

                        // decode distance base of block to copy
                        while (k < (15)) { // max bits for distance code
                            n--;
                            b |= (z.read_byte(p++) & 0xff) << k;
                            k += 8;
                        }

                        t = b & md;
                        tp = td;
                        tp_index = td_index;
                        tp_index_t_3 = (tp_index + t) * 3;
                        e = tp[tp_index_t_3];

                        do {

                            b >>= (tp[tp_index_t_3 + 1]);
                            k -= (tp[tp_index_t_3 + 1]);

                            if ((e & 16) !== 0) {
                                // get extra bits to add to distance base
                                e &= 15;
                                while (k < (e)) { // get extra bits (up to 13)
                                    n--;
                                    b |= (z.read_byte(p++) & 0xff) << k;
                                    k += 8;
                                }

                                d = tp[tp_index_t_3 + 2] + (b & inflate_mask[e]);

                                b >>= (e);
                                k -= (e);

                                // do the copy
                                m -= c;
                                if (q >= d) { // offset before dest
                                    // just copy
                                    r = q - d;
                                    if (q - r > 0 && 2 > (q - r)) {
                                        s.window[q++] = s.window[r++]; // minimum
                                        // count is
                                        // three,
                                        s.window[q++] = s.window[r++]; // so unroll
                                        // loop a
                                        // little
                                        c -= 2;
                                    } else {
                                        s.window.set(s.window.subarray(r, r + 2), q);
                                        q += 2;
                                        r += 2;
                                        c -= 2;
                                    }
                                } else { // else offset after destination
                                    r = q - d;
                                    do {
                                        r += s.end; // force pointer in window
                                    } while (r < 0); // covers invalid distances
                                    e = s.end - r;
                                    if (c > e) { // if source crosses,
                                        c -= e; // wrapped copy
                                        if (q - r > 0 && e > (q - r)) {
                                            do {
                                                s.window[q++] = s.window[r++];
                                            } while (--e !== 0);
                                        } else {
                                            s.window.set(s.window.subarray(r, r + e), q);
                                            q += e;
                                            r += e;
                                            e = 0;
                                        }
                                        r = 0; // copy rest from start of window
                                    }

                                }

                                // copy all or what's left
                                if (q - r > 0 && c > (q - r)) {
                                    do {
                                        s.window[q++] = s.window[r++];
                                    } while (--c !== 0);
                                } else {
                                    s.window.set(s.window.subarray(r, r + c), q);
                                    q += c;
                                    r += c;
                                    c = 0;
                                }
                                break;
                            } else if ((e & 64) === 0) {
                                t += tp[tp_index_t_3 + 2];
                                t += (b & inflate_mask[e]);
                                tp_index_t_3 = (tp_index + t) * 3;
                                e = tp[tp_index_t_3];
                            } else {
                                z.msg = "invalid distance code";

                                c = z.avail_in - n;
                                c = (k >> 3) < c ? k >> 3 : c;
                                n += c;
                                p -= c;
                                k -= c << 3;

                                s.bitb = b;
                                s.bitk = k;
                                z.avail_in = n;
                                z.total_in += p - z.next_in_index;
                                z.next_in_index = p;
                                s.write = q;

                                return Z_DATA_ERROR;
                            }
                        } while (true);
                        break;
                    }

                    if ((e & 64) === 0) {
                        t += tp[tp_index_t_3 + 2];
                        t += (b & inflate_mask[e]);
                        tp_index_t_3 = (tp_index + t) * 3;
                        if ((e = tp[tp_index_t_3]) === 0) {

                            b >>= (tp[tp_index_t_3 + 1]);
                            k -= (tp[tp_index_t_3 + 1]);

                            s.window[q++] = /* (byte) */tp[tp_index_t_3 + 2];
                            m--;
                            break;
                        }
                    } else if ((e & 32) !== 0) {

                        c = z.avail_in - n;
                        c = (k >> 3) < c ? k >> 3 : c;
                        n += c;
                        p -= c;
                        k -= c << 3;

                        s.bitb = b;
                        s.bitk = k;
                        z.avail_in = n;
                        z.total_in += p - z.next_in_index;
                        z.next_in_index = p;
                        s.write = q;

                        return Z_STREAM_END;
                    } else {
                        z.msg = "invalid literal/length code";

                        c = z.avail_in - n;
                        c = (k >> 3) < c ? k >> 3 : c;
                        n += c;
                        p -= c;
                        k -= c << 3;

                        s.bitb = b;
                        s.bitk = k;
                        z.avail_in = n;
                        z.total_in += p - z.next_in_index;
                        z.next_in_index = p;
                        s.write = q;

                        return Z_DATA_ERROR;
                    }
                } while (true);
            } while (m >= 258 && n >= 10);

            // not enough input or output--restore pointers and return
            c = z.avail_in - n;
            c = (k >> 3) < c ? k >> 3 : c;
            n += c;
            p -= c;
            k -= c << 3;

            s.bitb = b;
            s.bitk = k;
            z.avail_in = n;
            z.total_in += p - z.next_in_index;
            z.next_in_index = p;
            s.write = q;

            return Z_OK;
        }

        that.init = function(bl, bd, tl, tl_index, td, td_index) {
            mode = START;
            lbits = /* (byte) */bl;
            dbits = /* (byte) */bd;
            ltree = tl;
            ltree_index = tl_index;
            dtree = td;
            dtree_index = td_index;
            tree = null;
        };

        that.proc = function(s, z, r) {
            var j; // temporary storage
            var tindex; // temporary pointer
            var e; // extra bits or operation
            var b = 0; // bit buffer
            var k = 0; // bits in bit buffer
            var p = 0; // input data pointer
            var n; // bytes available there
            var q; // output window write pointer
            var m; // bytes to end of window or read pointer
            var f; // pointer to copy strings from

            // copy input/output information to locals (UPDATE macro restores)
            p = z.next_in_index;
            n = z.avail_in;
            b = s.bitb;
            k = s.bitk;
            q = s.write;
            m = q < s.read ? s.read - q - 1 : s.end - q;

            // process input and output based on current state
            while (true) {
                switch (mode) {
                    // waiting for "i:"=input, "o:"=output, "x:"=nothing
                    case START: // x: set up for LEN
                        if (m >= 258 && n >= 10) {

                            s.bitb = b;
                            s.bitk = k;
                            z.avail_in = n;
                            z.total_in += p - z.next_in_index;
                            z.next_in_index = p;
                            s.write = q;
                            r = inflate_fast(lbits, dbits, ltree, ltree_index, dtree, dtree_index, s, z);

                            p = z.next_in_index;
                            n = z.avail_in;
                            b = s.bitb;
                            k = s.bitk;
                            q = s.write;
                            m = q < s.read ? s.read - q - 1 : s.end - q;

                            if (r != Z_OK) {
                                mode = r == Z_STREAM_END ? WASH : BADCODE;
                                break;
                            }
                        }
                        need = lbits;
                        tree = ltree;
                        tree_index = ltree_index;

                        mode = LEN;
                    case LEN: // i: get length/literal/eob next
                        j = need;

                        while (k < (j)) {
                            if (n !== 0)
                                r = Z_OK;
                            else {

                                s.bitb = b;
                                s.bitk = k;
                                z.avail_in = n;
                                z.total_in += p - z.next_in_index;
                                z.next_in_index = p;
                                s.write = q;
                                return s.inflate_flush(z, r);
                            }
                            n--;
                            b |= (z.read_byte(p++) & 0xff) << k;
                            k += 8;
                        }

                        tindex = (tree_index + (b & inflate_mask[j])) * 3;

                        b >>>= (tree[tindex + 1]);
                        k -= (tree[tindex + 1]);

                        e = tree[tindex];

                        if (e === 0) { // literal
                            lit = tree[tindex + 2];
                            mode = LIT;
                            break;
                        }
                        if ((e & 16) !== 0) { // length
                            get = e & 15;
                            len = tree[tindex + 2];
                            mode = LENEXT;
                            break;
                        }
                        if ((e & 64) === 0) { // next table
                            need = e;
                            tree_index = tindex / 3 + tree[tindex + 2];
                            break;
                        }
                        if ((e & 32) !== 0) { // end of block
                            mode = WASH;
                            break;
                        }
                        mode = BADCODE; // invalid code
                        z.msg = "invalid literal/length code";
                        r = Z_DATA_ERROR;

                        s.bitb = b;
                        s.bitk = k;
                        z.avail_in = n;
                        z.total_in += p - z.next_in_index;
                        z.next_in_index = p;
                        s.write = q;
                        return s.inflate_flush(z, r);

                    case LENEXT: // i: getting length extra (have base)
                        j = get;

                        while (k < (j)) {
                            if (n !== 0)
                                r = Z_OK;
                            else {

                                s.bitb = b;
                                s.bitk = k;
                                z.avail_in = n;
                                z.total_in += p - z.next_in_index;
                                z.next_in_index = p;
                                s.write = q;
                                return s.inflate_flush(z, r);
                            }
                            n--;
                            b |= (z.read_byte(p++) & 0xff) << k;
                            k += 8;
                        }

                        len += (b & inflate_mask[j]);

                        b >>= j;
                        k -= j;

                        need = dbits;
                        tree = dtree;
                        tree_index = dtree_index;
                        mode = DIST;
                    case DIST: // i: get distance next
                        j = need;

                        while (k < (j)) {
                            if (n !== 0)
                                r = Z_OK;
                            else {

                                s.bitb = b;
                                s.bitk = k;
                                z.avail_in = n;
                                z.total_in += p - z.next_in_index;
                                z.next_in_index = p;
                                s.write = q;
                                return s.inflate_flush(z, r);
                            }
                            n--;
                            b |= (z.read_byte(p++) & 0xff) << k;
                            k += 8;
                        }

                        tindex = (tree_index + (b & inflate_mask[j])) * 3;

                        b >>= tree[tindex + 1];
                        k -= tree[tindex + 1];

                        e = (tree[tindex]);
                        if ((e & 16) !== 0) { // distance
                            get = e & 15;
                            dist = tree[tindex + 2];
                            mode = DISTEXT;
                            break;
                        }
                        if ((e & 64) === 0) { // next table
                            need = e;
                            tree_index = tindex / 3 + tree[tindex + 2];
                            break;
                        }
                        mode = BADCODE; // invalid code
                        z.msg = "invalid distance code";
                        r = Z_DATA_ERROR;

                        s.bitb = b;
                        s.bitk = k;
                        z.avail_in = n;
                        z.total_in += p - z.next_in_index;
                        z.next_in_index = p;
                        s.write = q;
                        return s.inflate_flush(z, r);

                    case DISTEXT: // i: getting distance extra
                        j = get;

                        while (k < (j)) {
                            if (n !== 0)
                                r = Z_OK;
                            else {

                                s.bitb = b;
                                s.bitk = k;
                                z.avail_in = n;
                                z.total_in += p - z.next_in_index;
                                z.next_in_index = p;
                                s.write = q;
                                return s.inflate_flush(z, r);
                            }
                            n--;
                            b |= (z.read_byte(p++) & 0xff) << k;
                            k += 8;
                        }

                        dist += (b & inflate_mask[j]);

                        b >>= j;
                        k -= j;

                        mode = COPY;
                    case COPY: // o: copying bytes in window, waiting for space
                        f = q - dist;
                        while (f < 0) { // modulo window size-"while" instead
                            f += s.end; // of "if" handles invalid distances
                        }
                        while (len !== 0) {

                            if (m === 0) {
                                if (q == s.end && s.read !== 0) {
                                    q = 0;
                                    m = q < s.read ? s.read - q - 1 : s.end - q;
                                }
                                if (m === 0) {
                                    s.write = q;
                                    r = s.inflate_flush(z, r);
                                    q = s.write;
                                    m = q < s.read ? s.read - q - 1 : s.end - q;

                                    if (q == s.end && s.read !== 0) {
                                        q = 0;
                                        m = q < s.read ? s.read - q - 1 : s.end - q;
                                    }

                                    if (m === 0) {
                                        s.bitb = b;
                                        s.bitk = k;
                                        z.avail_in = n;
                                        z.total_in += p - z.next_in_index;
                                        z.next_in_index = p;
                                        s.write = q;
                                        return s.inflate_flush(z, r);
                                    }
                                }
                            }

                            s.window[q++] = s.window[f++];
                            m--;

                            if (f == s.end)
                                f = 0;
                            len--;
                        }
                        mode = START;
                        break;
                    case LIT: // o: got literal, waiting for output space
                        if (m === 0) {
                            if (q == s.end && s.read !== 0) {
                                q = 0;
                                m = q < s.read ? s.read - q - 1 : s.end - q;
                            }
                            if (m === 0) {
                                s.write = q;
                                r = s.inflate_flush(z, r);
                                q = s.write;
                                m = q < s.read ? s.read - q - 1 : s.end - q;

                                if (q == s.end && s.read !== 0) {
                                    q = 0;
                                    m = q < s.read ? s.read - q - 1 : s.end - q;
                                }
                                if (m === 0) {
                                    s.bitb = b;
                                    s.bitk = k;
                                    z.avail_in = n;
                                    z.total_in += p - z.next_in_index;
                                    z.next_in_index = p;
                                    s.write = q;
                                    return s.inflate_flush(z, r);
                                }
                            }
                        }
                        r = Z_OK;

                        s.window[q++] = /* (byte) */lit;
                        m--;

                        mode = START;
                        break;
                    case WASH: // o: got eob, possibly more output
                        if (k > 7) { // return unused byte, if any
                            k -= 8;
                            n++;
                            p--; // can always return one
                        }

                        s.write = q;
                        r = s.inflate_flush(z, r);
                        q = s.write;
                        m = q < s.read ? s.read - q - 1 : s.end - q;

                        if (s.read != s.write) {
                            s.bitb = b;
                            s.bitk = k;
                            z.avail_in = n;
                            z.total_in += p - z.next_in_index;
                            z.next_in_index = p;
                            s.write = q;
                            return s.inflate_flush(z, r);
                        }
                        mode = END;
                    case END:
                        r = Z_STREAM_END;
                        s.bitb = b;
                        s.bitk = k;
                        z.avail_in = n;
                        z.total_in += p - z.next_in_index;
                        z.next_in_index = p;
                        s.write = q;
                        return s.inflate_flush(z, r);

                    case BADCODE: // x: got error

                        r = Z_DATA_ERROR;

                        s.bitb = b;
                        s.bitk = k;
                        z.avail_in = n;
                        z.total_in += p - z.next_in_index;
                        z.next_in_index = p;
                        s.write = q;
                        return s.inflate_flush(z, r);

                    default:
                        r = Z_STREAM_ERROR;

                        s.bitb = b;
                        s.bitk = k;
                        z.avail_in = n;
                        z.total_in += p - z.next_in_index;
                        z.next_in_index = p;
                        s.write = q;
                        return s.inflate_flush(z, r);
                }
            }
        };

        that.free = function() {
            // ZFREE(z, c);
        };

    }

    // InfBlocks

    // Table for deflate from PKZIP's appnote.txt.
    var border = [ // Order of the bit length code lengths
        16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15 ];

    var TYPE = 0; // get type bits (3, including end bit)
    var LENS = 1; // get lengths for stored
    var STORED = 2;// processing stored block
    var TABLE = 3; // get table lengths
    var BTREE = 4; // get bit lengths tree for a dynamic
    // block
    var DTREE = 5; // get length, distance trees for a
    // dynamic block
    var CODES = 6; // processing fixed or dynamic block
    var DRY = 7; // output remaining window bytes
    var DONELOCKS = 8; // finished last block, done
    var BADBLOCKS = 9; // ot a data error--stuck here

    function InfBlocks(z, w) {
        var that = this;

        var mode = TYPE; // current inflate_block mode

        var left = 0; // if STORED, bytes left to copy

        var table = 0; // table lengths (14 bits)
        var index = 0; // index into blens (or border)
        var blens; // bit lengths of codes
        var bb = [ 0 ]; // bit length tree depth
        var tb = [ 0 ]; // bit length decoding tree

        var codes = new InfCodes(); // if CODES, current state

        var last = 0; // true if this block is the last block

        var hufts = new Int32Array(MANY * 3); // single malloc for tree space
        var check = 0; // check on output
        var inftree = new InfTree();

        that.bitk = 0; // bits in bit buffer
        that.bitb = 0; // bit buffer
        that.window = new Uint8Array(w); // sliding window
        that.end = w; // one byte after sliding window
        that.read = 0; // window read pointer
        that.write = 0; // window write pointer

        that.reset = function(z, c) {
            if (c)
                c[0] = check;
            // if (mode == BTREE || mode == DTREE) {
            // }
            if (mode == CODES) {
                codes.free(z);
            }
            mode = TYPE;
            that.bitk = 0;
            that.bitb = 0;
            that.read = that.write = 0;
        };

        that.reset(z, null);

        // copy as much as possible from the sliding window to the output area
        that.inflate_flush = function(z, r) {
            var n;
            var p;
            var q;

            // local copies of source and destination pointers
            p = z.next_out_index;
            q = that.read;

            // compute number of bytes to copy as far as end of window
            n = /* (int) */((q <= that.write ? that.write : that.end) - q);
            if (n > z.avail_out)
                n = z.avail_out;
            if (n !== 0 && r == Z_BUF_ERROR)
                r = Z_OK;

            // update counters
            z.avail_out -= n;
            z.total_out += n;

            // copy as far as end of window
            z.next_out.set(that.window.subarray(q, q + n), p);
            p += n;
            q += n;

            // see if more to copy at beginning of window
            if (q == that.end) {
                // wrap pointers
                q = 0;
                if (that.write == that.end)
                    that.write = 0;

                // compute bytes to copy
                n = that.write - q;
                if (n > z.avail_out)
                    n = z.avail_out;
                if (n !== 0 && r == Z_BUF_ERROR)
                    r = Z_OK;

                // update counters
                z.avail_out -= n;
                z.total_out += n;

                // copy
                z.next_out.set(that.window.subarray(q, q + n), p);
                p += n;
                q += n;
            }

            // update pointers
            z.next_out_index = p;
            that.read = q;

            // done
            return r;
        };

        that.proc = function(z, r) {
            var t; // temporary storage
            var b; // bit buffer
            var k; // bits in bit buffer
            var p; // input data pointer
            var n; // bytes available there
            var q; // output window write pointer
            var m; // bytes to end of window or read pointer

            var i;

            // copy input/output information to locals (UPDATE macro restores)
            // {
            p = z.next_in_index;
            n = z.avail_in;
            b = that.bitb;
            k = that.bitk;
            // }
            // {
            q = that.write;
            m = /* (int) */(q < that.read ? that.read - q - 1 : that.end - q);
            // }

            // process input based on current state
            // DEBUG dtree
            while (true) {
                switch (mode) {
                    case TYPE:

                        while (k < (3)) {
                            if (n !== 0) {
                                r = Z_OK;
                            } else {
                                that.bitb = b;
                                that.bitk = k;
                                z.avail_in = n;
                                z.total_in += p - z.next_in_index;
                                z.next_in_index = p;
                                that.write = q;
                                return that.inflate_flush(z, r);
                            }
                            n--;
                            b |= (z.read_byte(p++) & 0xff) << k;
                            k += 8;
                        }
                        t = /* (int) */(b & 7);
                        last = t & 1;

                        switch (t >>> 1) {
                            case 0: // stored
                                // {
                                b >>>= (3);
                                k -= (3);
                                // }
                                t = k & 7; // go to byte boundary

                                // {
                                b >>>= (t);
                                k -= (t);
                                // }
                                mode = LENS; // get length of stored block
                                break;
                            case 1: // fixed
                                // {
                                var bl = []; // new Array(1);
                                var bd = []; // new Array(1);
                                var tl = [ [] ]; // new Array(1);
                                var td = [ [] ]; // new Array(1);

                                InfTree.inflate_trees_fixed(bl, bd, tl, td);
                                codes.init(bl[0], bd[0], tl[0], 0, td[0], 0);
                                // }

                                // {
                                b >>>= (3);
                                k -= (3);
                                // }

                                mode = CODES;
                                break;
                            case 2: // dynamic

                                // {
                                b >>>= (3);
                                k -= (3);
                                // }

                                mode = TABLE;
                                break;
                            case 3: // illegal

                                // {
                                b >>>= (3);
                                k -= (3);
                                // }
                                mode = BADBLOCKS;
                                z.msg = "invalid block type";
                                r = Z_DATA_ERROR;

                                that.bitb = b;
                                that.bitk = k;
                                z.avail_in = n;
                                z.total_in += p - z.next_in_index;
                                z.next_in_index = p;
                                that.write = q;
                                return that.inflate_flush(z, r);
                        }
                        break;
                    case LENS:

                        while (k < (32)) {
                            if (n !== 0) {
                                r = Z_OK;
                            } else {
                                that.bitb = b;
                                that.bitk = k;
                                z.avail_in = n;
                                z.total_in += p - z.next_in_index;
                                z.next_in_index = p;
                                that.write = q;
                                return that.inflate_flush(z, r);
                            }
                            n--;
                            b |= (z.read_byte(p++) & 0xff) << k;
                            k += 8;
                        }

                        if ((((~b) >>> 16) & 0xffff) != (b & 0xffff)) {
                            mode = BADBLOCKS;
                            z.msg = "invalid stored block lengths";
                            r = Z_DATA_ERROR;

                            that.bitb = b;
                            that.bitk = k;
                            z.avail_in = n;
                            z.total_in += p - z.next_in_index;
                            z.next_in_index = p;
                            that.write = q;
                            return that.inflate_flush(z, r);
                        }
                        left = (b & 0xffff);
                        b = k = 0; // dump bits
                        mode = left !== 0 ? STORED : (last !== 0 ? DRY : TYPE);
                        break;
                    case STORED:
                        if (n === 0) {
                            that.bitb = b;
                            that.bitk = k;
                            z.avail_in = n;
                            z.total_in += p - z.next_in_index;
                            z.next_in_index = p;
                            that.write = q;
                            return that.inflate_flush(z, r);
                        }

                        if (m === 0) {
                            if (q == that.end && that.read !== 0) {
                                q = 0;
                                m = /* (int) */(q < that.read ? that.read - q - 1 : that.end - q);
                            }
                            if (m === 0) {
                                that.write = q;
                                r = that.inflate_flush(z, r);
                                q = that.write;
                                m = /* (int) */(q < that.read ? that.read - q - 1 : that.end - q);
                                if (q == that.end && that.read !== 0) {
                                    q = 0;
                                    m = /* (int) */(q < that.read ? that.read - q - 1 : that.end - q);
                                }
                                if (m === 0) {
                                    that.bitb = b;
                                    that.bitk = k;
                                    z.avail_in = n;
                                    z.total_in += p - z.next_in_index;
                                    z.next_in_index = p;
                                    that.write = q;
                                    return that.inflate_flush(z, r);
                                }
                            }
                        }
                        r = Z_OK;

                        t = left;
                        if (t > n)
                            t = n;
                        if (t > m)
                            t = m;
                        that.window.set(z.read_buf(p, t), q);
                        p += t;
                        n -= t;
                        q += t;
                        m -= t;
                        if ((left -= t) !== 0)
                            break;
                        mode = last !== 0 ? DRY : TYPE;
                        break;
                    case TABLE:

                        while (k < (14)) {
                            if (n !== 0) {
                                r = Z_OK;
                            } else {
                                that.bitb = b;
                                that.bitk = k;
                                z.avail_in = n;
                                z.total_in += p - z.next_in_index;
                                z.next_in_index = p;
                                that.write = q;
                                return that.inflate_flush(z, r);
                            }

                            n--;
                            b |= (z.read_byte(p++) & 0xff) << k;
                            k += 8;
                        }

                        table = t = (b & 0x3fff);
                        if ((t & 0x1f) > 29 || ((t >> 5) & 0x1f) > 29) {
                            mode = BADBLOCKS;
                            z.msg = "too many length or distance symbols";
                            r = Z_DATA_ERROR;

                            that.bitb = b;
                            that.bitk = k;
                            z.avail_in = n;
                            z.total_in += p - z.next_in_index;
                            z.next_in_index = p;
                            that.write = q;
                            return that.inflate_flush(z, r);
                        }
                        t = 258 + (t & 0x1f) + ((t >> 5) & 0x1f);
                        if (!blens || blens.length < t) {
                            blens = []; // new Array(t);
                        } else {
                            for (i = 0; i < t; i++) {
                                blens[i] = 0;
                            }
                        }

                        // {
                        b >>>= (14);
                        k -= (14);
                        // }

                        index = 0;
                        mode = BTREE;
                    case BTREE:
                        while (index < 4 + (table >>> 10)) {
                            while (k < (3)) {
                                if (n !== 0) {
                                    r = Z_OK;
                                } else {
                                    that.bitb = b;
                                    that.bitk = k;
                                    z.avail_in = n;
                                    z.total_in += p - z.next_in_index;
                                    z.next_in_index = p;
                                    that.write = q;
                                    return that.inflate_flush(z, r);
                                }
                                n--;
                                b |= (z.read_byte(p++) & 0xff) << k;
                                k += 8;
                            }

                            blens[border[index++]] = b & 7;

                            // {
                            b >>>= (3);
                            k -= (3);
                            // }
                        }

                        while (index < 19) {
                            blens[border[index++]] = 0;
                        }

                        bb[0] = 7;
                        t = inftree.inflate_trees_bits(blens, bb, tb, hufts, z);
                        if (t != Z_OK) {
                            r = t;
                            if (r == Z_DATA_ERROR) {
                                blens = null;
                                mode = BADBLOCKS;
                            }

                            that.bitb = b;
                            that.bitk = k;
                            z.avail_in = n;
                            z.total_in += p - z.next_in_index;
                            z.next_in_index = p;
                            that.write = q;
                            return that.inflate_flush(z, r);
                        }

                        index = 0;
                        mode = DTREE;
                    case DTREE:
                        while (true) {
                            t = table;
                            if (!(index < 258 + (t & 0x1f) + ((t >> 5) & 0x1f))) {
                                break;
                            }

                            var j, c;

                            t = bb[0];

                            while (k < (t)) {
                                if (n !== 0) {
                                    r = Z_OK;
                                } else {
                                    that.bitb = b;
                                    that.bitk = k;
                                    z.avail_in = n;
                                    z.total_in += p - z.next_in_index;
                                    z.next_in_index = p;
                                    that.write = q;
                                    return that.inflate_flush(z, r);
                                }
                                n--;
                                b |= (z.read_byte(p++) & 0xff) << k;
                                k += 8;
                            }

                            // if (tb[0] == -1) {
                            // System.err.println("null...");
                            // }

                            t = hufts[(tb[0] + (b & inflate_mask[t])) * 3 + 1];
                            c = hufts[(tb[0] + (b & inflate_mask[t])) * 3 + 2];

                            if (c < 16) {
                                b >>>= (t);
                                k -= (t);
                                blens[index++] = c;
                            } else { // c == 16..18
                                i = c == 18 ? 7 : c - 14;
                                j = c == 18 ? 11 : 3;

                                while (k < (t + i)) {
                                    if (n !== 0) {
                                        r = Z_OK;
                                    } else {
                                        that.bitb = b;
                                        that.bitk = k;
                                        z.avail_in = n;
                                        z.total_in += p - z.next_in_index;
                                        z.next_in_index = p;
                                        that.write = q;
                                        return that.inflate_flush(z, r);
                                    }
                                    n--;
                                    b |= (z.read_byte(p++) & 0xff) << k;
                                    k += 8;
                                }

                                b >>>= (t);
                                k -= (t);

                                j += (b & inflate_mask[i]);

                                b >>>= (i);
                                k -= (i);

                                i = index;
                                t = table;
                                if (i + j > 258 + (t & 0x1f) + ((t >> 5) & 0x1f) || (c == 16 && i < 1)) {
                                    blens = null;
                                    mode = BADBLOCKS;
                                    z.msg = "invalid bit length repeat";
                                    r = Z_DATA_ERROR;

                                    that.bitb = b;
                                    that.bitk = k;
                                    z.avail_in = n;
                                    z.total_in += p - z.next_in_index;
                                    z.next_in_index = p;
                                    that.write = q;
                                    return that.inflate_flush(z, r);
                                }

                                c = c == 16 ? blens[i - 1] : 0;
                                do {
                                    blens[i++] = c;
                                } while (--j !== 0);
                                index = i;
                            }
                        }

                        tb[0] = -1;
                        // {
                        var bl_ = []; // new Array(1);
                        var bd_ = []; // new Array(1);
                        var tl_ = []; // new Array(1);
                        var td_ = []; // new Array(1);
                        bl_[0] = 9; // must be <= 9 for lookahead assumptions
                        bd_[0] = 6; // must be <= 9 for lookahead assumptions

                        t = table;
                        t = inftree.inflate_trees_dynamic(257 + (t & 0x1f), 1 + ((t >> 5) & 0x1f), blens, bl_, bd_, tl_, td_, hufts, z);

                        if (t != Z_OK) {
                            if (t == Z_DATA_ERROR) {
                                blens = null;
                                mode = BADBLOCKS;
                            }
                            r = t;

                            that.bitb = b;
                            that.bitk = k;
                            z.avail_in = n;
                            z.total_in += p - z.next_in_index;
                            z.next_in_index = p;
                            that.write = q;
                            return that.inflate_flush(z, r);
                        }
                        codes.init(bl_[0], bd_[0], hufts, tl_[0], hufts, td_[0]);
                        // }
                        mode = CODES;
                    case CODES:
                        that.bitb = b;
                        that.bitk = k;
                        z.avail_in = n;
                        z.total_in += p - z.next_in_index;
                        z.next_in_index = p;
                        that.write = q;

                        if ((r = codes.proc(that, z, r)) != Z_STREAM_END) {
                            return that.inflate_flush(z, r);
                        }
                        r = Z_OK;
                        codes.free(z);

                        p = z.next_in_index;
                        n = z.avail_in;
                        b = that.bitb;
                        k = that.bitk;
                        q = that.write;
                        m = /* (int) */(q < that.read ? that.read - q - 1 : that.end - q);

                        if (last === 0) {
                            mode = TYPE;
                            break;
                        }
                        mode = DRY;
                    case DRY:
                        that.write = q;
                        r = that.inflate_flush(z, r);
                        q = that.write;
                        m = /* (int) */(q < that.read ? that.read - q - 1 : that.end - q);
                        if (that.read != that.write) {
                            that.bitb = b;
                            that.bitk = k;
                            z.avail_in = n;
                            z.total_in += p - z.next_in_index;
                            z.next_in_index = p;
                            that.write = q;
                            return that.inflate_flush(z, r);
                        }
                        mode = DONELOCKS;
                    case DONELOCKS:
                        r = Z_STREAM_END;

                        that.bitb = b;
                        that.bitk = k;
                        z.avail_in = n;
                        z.total_in += p - z.next_in_index;
                        z.next_in_index = p;
                        that.write = q;
                        return that.inflate_flush(z, r);
                    case BADBLOCKS:
                        r = Z_DATA_ERROR;

                        that.bitb = b;
                        that.bitk = k;
                        z.avail_in = n;
                        z.total_in += p - z.next_in_index;
                        z.next_in_index = p;
                        that.write = q;
                        return that.inflate_flush(z, r);

                    default:
                        r = Z_STREAM_ERROR;

                        that.bitb = b;
                        that.bitk = k;
                        z.avail_in = n;
                        z.total_in += p - z.next_in_index;
                        z.next_in_index = p;
                        that.write = q;
                        return that.inflate_flush(z, r);
                }
            }
        };

        that.free = function(z) {
            that.reset(z, null);
            that.window = null;
            hufts = null;
            // ZFREE(z, s);
        };

        that.set_dictionary = function(d, start, n) {
            that.window.set(d.subarray(start, start + n), 0);
            that.read = that.write = n;
        };

        // Returns true if inflate is currently at the end of a block generated
        // by Z_SYNC_FLUSH or Z_FULL_FLUSH.
        that.sync_point = function() {
            return mode == LENS ? 1 : 0;
        };

    }

    // Inflate

    // preset dictionary flag in zlib header
    var PRESET_DICT = 0x20;

    var Z_DEFLATED = 8;

    var METHOD = 0; // waiting for method byte
    var FLAG = 1; // waiting for flag byte
    var DICT4 = 2; // four dictionary check bytes to go
    var DICT3 = 3; // three dictionary check bytes to go
    var DICT2 = 4; // two dictionary check bytes to go
    var DICT1 = 5; // one dictionary check byte to go
    var DICT0 = 6; // waiting for inflateSetDictionary
    var BLOCKS = 7; // decompressing blocks
    var DONE = 12; // finished check, done
    var BAD = 13; // got an error--stay here

    var mark = [ 0, 0, 0xff, 0xff ];

    function Inflate() {
        var that = this;

        that.mode = 0; // current inflate mode

        // mode dependent information
        that.method = 0; // if FLAGS, method byte

        // if CHECK, check values to compare
        that.was = [ 0 ]; // new Array(1); // computed check value
        that.need = 0; // stream check value

        // if BAD, inflateSync's marker bytes count
        that.marker = 0;

        // mode independent information
        that.wbits = 0; // log2(window size) (8..15, defaults to 15)

        // this.blocks; // current inflate_blocks state

        function inflateReset(z) {
            if (!z || !z.istate)
                return Z_STREAM_ERROR;

            z.total_in = z.total_out = 0;
            z.msg = null;
            z.istate.mode = BLOCKS;
            z.istate.blocks.reset(z, null);
            return Z_OK;
        }

        that.inflateEnd = function(z) {
            if (that.blocks)
                that.blocks.free(z);
            that.blocks = null;
            // ZFREE(z, z->state);
            return Z_OK;
        };

        that.inflateInit = function(z, w) {
            z.msg = null;
            that.blocks = null;

            // set window size
            if (w < 8 || w > 15) {
                that.inflateEnd(z);
                return Z_STREAM_ERROR;
            }
            that.wbits = w;

            z.istate.blocks = new InfBlocks(z, 1 << w);

            // reset state
            inflateReset(z);
            return Z_OK;
        };

        that.inflate = function(z, f) {
            var r;
            var b;

            if (!z || !z.istate || !z.next_in)
                return Z_STREAM_ERROR;
            f = f == Z_FINISH ? Z_BUF_ERROR : Z_OK;
            r = Z_BUF_ERROR;
            while (true) {
                // System.out.println("mode: "+z.istate.mode);
                switch (z.istate.mode) {
                    case METHOD:

                        if (z.avail_in === 0)
                            return r;
                        r = f;

                        z.avail_in--;
                        z.total_in++;
                        if (((z.istate.method = z.read_byte(z.next_in_index++)) & 0xf) != Z_DEFLATED) {
                            z.istate.mode = BAD;
                            z.msg = "unknown compression method";
                            z.istate.marker = 5; // can't try inflateSync
                            break;
                        }
                        if ((z.istate.method >> 4) + 8 > z.istate.wbits) {
                            z.istate.mode = BAD;
                            z.msg = "invalid window size";
                            z.istate.marker = 5; // can't try inflateSync
                            break;
                        }
                        z.istate.mode = FLAG;
                    case FLAG:

                        if (z.avail_in === 0)
                            return r;
                        r = f;

                        z.avail_in--;
                        z.total_in++;
                        b = (z.read_byte(z.next_in_index++)) & 0xff;

                        if ((((z.istate.method << 8) + b) % 31) !== 0) {
                            z.istate.mode = BAD;
                            z.msg = "incorrect header check";
                            z.istate.marker = 5; // can't try inflateSync
                            break;
                        }

                        if ((b & PRESET_DICT) === 0) {
                            z.istate.mode = BLOCKS;
                            break;
                        }
                        z.istate.mode = DICT4;
                    case DICT4:

                        if (z.avail_in === 0)
                            return r;
                        r = f;

                        z.avail_in--;
                        z.total_in++;
                        z.istate.need = ((z.read_byte(z.next_in_index++) & 0xff) << 24) & 0xff000000;
                        z.istate.mode = DICT3;
                    case DICT3:

                        if (z.avail_in === 0)
                            return r;
                        r = f;

                        z.avail_in--;
                        z.total_in++;
                        z.istate.need += ((z.read_byte(z.next_in_index++) & 0xff) << 16) & 0xff0000;
                        z.istate.mode = DICT2;
                    case DICT2:

                        if (z.avail_in === 0)
                            return r;
                        r = f;

                        z.avail_in--;
                        z.total_in++;
                        z.istate.need += ((z.read_byte(z.next_in_index++) & 0xff) << 8) & 0xff00;
                        z.istate.mode = DICT1;
                    case DICT1:

                        if (z.avail_in === 0)
                            return r;
                        r = f;

                        z.avail_in--;
                        z.total_in++;
                        z.istate.need += (z.read_byte(z.next_in_index++) & 0xff);
                        z.istate.mode = DICT0;
                        return Z_NEED_DICT;
                    case DICT0:
                        z.istate.mode = BAD;
                        z.msg = "need dictionary";
                        z.istate.marker = 0; // can try inflateSync
                        return Z_STREAM_ERROR;
                    case BLOCKS:

                        r = z.istate.blocks.proc(z, r);
                        if (r == Z_DATA_ERROR) {
                            z.istate.mode = BAD;
                            z.istate.marker = 0; // can try inflateSync
                            break;
                        }
                        if (r == Z_OK) {
                            r = f;
                        }
                        if (r != Z_STREAM_END) {
                            return r;
                        }
                        r = f;
                        z.istate.blocks.reset(z, z.istate.was);
                        z.istate.mode = DONE;
                    case DONE:
                        return Z_STREAM_END;
                    case BAD:
                        return Z_DATA_ERROR;
                    default:
                        return Z_STREAM_ERROR;
                }
            }
        };

        that.inflateSetDictionary = function(z, dictionary, dictLength) {
            var index = 0;
            var length = dictLength;
            if (!z || !z.istate || z.istate.mode != DICT0)
                return Z_STREAM_ERROR;

            if (length >= (1 << z.istate.wbits)) {
                length = (1 << z.istate.wbits) - 1;
                index = dictLength - length;
            }
            z.istate.blocks.set_dictionary(dictionary, index, length);
            z.istate.mode = BLOCKS;
            return Z_OK;
        };

        that.inflateSync = function(z) {
            var n; // number of bytes to look at
            var p; // pointer to bytes
            var m; // number of marker bytes found in a row
            var r, w; // temporaries to save total_in and total_out

            // set up
            if (!z || !z.istate)
                return Z_STREAM_ERROR;
            if (z.istate.mode != BAD) {
                z.istate.mode = BAD;
                z.istate.marker = 0;
            }
            if ((n = z.avail_in) === 0)
                return Z_BUF_ERROR;
            p = z.next_in_index;
            m = z.istate.marker;

            // search
            while (n !== 0 && m < 4) {
                if (z.read_byte(p) == mark[m]) {
                    m++;
                } else if (z.read_byte(p) !== 0) {
                    m = 0;
                } else {
                    m = 4 - m;
                }
                p++;
                n--;
            }

            // restore
            z.total_in += p - z.next_in_index;
            z.next_in_index = p;
            z.avail_in = n;
            z.istate.marker = m;

            // return no joy or set up to restart on a new block
            if (m != 4) {
                return Z_DATA_ERROR;
            }
            r = z.total_in;
            w = z.total_out;
            inflateReset(z);
            z.total_in = r;
            z.total_out = w;
            z.istate.mode = BLOCKS;
            return Z_OK;
        };

        // Returns true if inflate is currently at the end of a block generated
        // by Z_SYNC_FLUSH or Z_FULL_FLUSH. This function is used by one PPP
        // implementation to provide an additional safety check. PPP uses
        // Z_SYNC_FLUSH
        // but removes the length bytes of the resulting empty stored block. When
        // decompressing, PPP checks that at the end of input packet, inflate is
        // waiting for these length bytes.
        that.inflateSyncPoint = function(z) {
            if (!z || !z.istate || !z.istate.blocks)
                return Z_STREAM_ERROR;
            return z.istate.blocks.sync_point();
        };
    }

    // ZStream

    function ZStream() {
    }

    ZStream.prototype = {
        inflateInit : function(bits) {
            var that = this;
            that.istate = new Inflate();
            if (!bits)
                bits = MAX_BITS;
            return that.istate.inflateInit(that, bits);
        },

        inflate : function(f) {
            var that = this;
            if (!that.istate)
                return Z_STREAM_ERROR;
            return that.istate.inflate(that, f);
        },

        inflateEnd : function() {
            var that = this;
            if (!that.istate)
                return Z_STREAM_ERROR;
            var ret = that.istate.inflateEnd(that);
            that.istate = null;
            return ret;
        },

        inflateSync : function() {
            var that = this;
            if (!that.istate)
                return Z_STREAM_ERROR;
            return that.istate.inflateSync(that);
        },
        inflateSetDictionary : function(dictionary, dictLength) {
            var that = this;
            if (!that.istate)
                return Z_STREAM_ERROR;
            return that.istate.inflateSetDictionary(that, dictionary, dictLength);
        },
        read_byte : function(start) {
            var that = this;
            return that.next_in.subarray(start, start + 1)[0];
        },
        read_buf : function(start, size) {
            var that = this;
            return that.next_in.subarray(start, start + size);
        }
    };

    // Inflater

    function Inflater() {
        var that = this;
        var z = new ZStream();
        var bufsize = 512;
        var flush = Z_NO_FLUSH;
        var buf = new Uint8Array(bufsize);
        var nomoreinput = false;

        z.inflateInit();
        z.next_out = buf;

        that.append = function(data, onprogress) {
            var err, buffers = [], lastIndex = 0, bufferIndex = 0, bufferSize = 0, array;
            if (data.length === 0)
                return;
            z.next_in_index = 0;
            z.next_in = data;
            z.avail_in = data.length;
            do {
                z.next_out_index = 0;
                z.avail_out = bufsize;
                if ((z.avail_in === 0) && (!nomoreinput)) { // if buffer is empty and more input is available, refill it
                    z.next_in_index = 0;
                    nomoreinput = true;
                }
                err = z.inflate(flush);
                if (nomoreinput && (err == Z_BUF_ERROR))
                    return -1;
                if (err != Z_OK && err != Z_STREAM_END)
                    throw "inflating: " + z.msg;
                if ((nomoreinput || err == Z_STREAM_END) && (z.avail_in == data.length))
                    return -1;
                if (z.next_out_index)
                    if (z.next_out_index == bufsize)
                        buffers.push(new Uint8Array(buf));
                    else
                        buffers.push(new Uint8Array(buf.subarray(0, z.next_out_index)));
                bufferSize += z.next_out_index;
                if (onprogress && z.next_in_index > 0 && z.next_in_index != lastIndex) {
                    onprogress(z.next_in_index);
                    lastIndex = z.next_in_index;
                }
            } while (z.avail_in > 0 || z.avail_out === 0);
            array = new Uint8Array(bufferSize);
            buffers.forEach(function(chunk) {
                array.set(chunk, bufferIndex);
                bufferIndex += chunk.length;
            });
            return array;
        };
        that.flush = function() {
            z.inflateEnd();
        };
    }

    var inflater;

    if (obj.zip)
        obj.zip.Inflater = Inflater;
    else {
        inflater = new Inflater();
        obj.addEventListener("message", function(event) {
            var message = event.data;

            if (message.append)
                obj.postMessage({
                    onappend : true,
                    data : inflater.append(message.data, function(current) {
                        obj.postMessage({
                            progress : true,
                            current : current
                        });
                    })
                });
            if (message.flush) {
                inflater.flush();
                obj.postMessage({
                    onflush : true
                });
            }
        }, false);
    }

})(this);
/*
 Copyright (c) 2012 Gildas Lormeau. All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice,
 this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in
 the documentation and/or other materials provided with the distribution.

 3. The names of the authors may not be used to endorse or promote products
 derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JCRAFT,
 INC. OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

(function(obj) {

    var ERR_BAD_FORMAT = "File format is not recognized.";
    var ERR_ENCRYPTED = "File contains encrypted entry.";
    var ERR_ZIP64 = "File is using Zip64 (4gb+ file size).";
    var ERR_READ = "Error while reading zip file.";
    var ERR_WRITE = "Error while writing zip file.";
    var ERR_WRITE_DATA = "Error while writing file data.";
    var ERR_READ_DATA = "Error while reading file data.";
    var ERR_DUPLICATED_NAME = "File already exists.";
    var ERR_HTTP_RANGE = "HTTP Range not supported.";
    var CHUNK_SIZE = 512 * 1024;

    var INFLATE_JS = "inflate.js";
    var DEFLATE_JS = "deflate.js";

    var appendABViewSupported;
    try {
        appendABViewSupported = new Blob([ getDataHelper(0).view ]).size == 0;
    } catch (e) {
    }

    function Crc32() {
        var crc = -1, that = this;
        that.append = function(data) {
            var offset, table = that.table;
            for (offset = 0; offset < data.length; offset++)
                crc = (crc >>> 8) ^ table[(crc ^ data[offset]) & 0xFF];
        };
        that.get = function() {
            return ~crc;
        };
    }
    Crc32.prototype.table = (function() {
        var i, j, t, table = [];
        for (i = 0; i < 256; i++) {
            t = i;
            for (j = 0; j < 8; j++)
                if (t & 1)
                    t = (t >>> 1) ^ 0xEDB88320;
                else
                    t = t >>> 1;
            table[i] = t;
        }
        return table;
    })();

    function blobSlice(blob, index, length) {
        if (blob.slice)
            return blob.slice(index, index + length);
        else if (blob.webkitSlice)
            return blob.webkitSlice(index, index + length);
        else if (blob.mozSlice)
            return blob.mozSlice(index, index + length);
        else if (blob.msSlice)
            return blob.msSlice(index, index + length);
    }

    function getDataHelper(byteLength, bytes) {
        var dataBuffer, dataArray;
        dataBuffer = new ArrayBuffer(byteLength);
        dataArray = new Uint8Array(dataBuffer);
        if (bytes)
            dataArray.set(bytes, 0);
        return {
            buffer : dataBuffer,
            array : dataArray,
            view : new DataView(dataBuffer)
        };
    }

    // Readers
    function Reader() {
    }

    function TextReader(text) {
        var that = this, blobReader;

        function init(callback, onerror) {
            var blob = new Blob([ text ], {
                type : "text/plain"
            });
            blobReader = new BlobReader(blob);
            blobReader.init(function() {
                that.size = blobReader.size;
                callback();
            }, onerror);
        }

        function readUint8Array(index, length, callback, onerror) {
            blobReader.readUint8Array(index, length, callback, onerror);
        }

        that.size = 0;
        that.init = init;
        that.readUint8Array = readUint8Array;
    }
    TextReader.prototype = new Reader();
    TextReader.prototype.constructor = TextReader;

    function Data64URIReader(dataURI) {
        var that = this, dataStart;

        function init(callback) {
            var dataEnd = dataURI.length;
            while (dataURI.charAt(dataEnd - 1) == "=")
                dataEnd--;
            dataStart = dataURI.indexOf(",") + 1;
            that.size = Math.floor((dataEnd - dataStart) * 0.75);
            callback();
        }

        function readUint8Array(index, length, callback) {
            var i, data = getDataHelper(length);
            var start = Math.floor(index / 3) * 4;
            var end = Math.ceil((index + length) / 3) * 4;
            var bytes = obj.atob(dataURI.substring(start + dataStart, end + dataStart));
            var delta = index - Math.floor(start / 4) * 3;
            for (i = delta; i < delta + length; i++)
                data.array[i - delta] = bytes.charCodeAt(i);
            callback(data.array);
        }

        that.size = 0;
        that.init = init;
        that.readUint8Array = readUint8Array;
    }
    Data64URIReader.prototype = new Reader();
    Data64URIReader.prototype.constructor = Data64URIReader;

    function BlobReader(blob) {
        var that = this;

        function init(callback) {
            this.size = blob.size;
            callback();
        }

        function readUint8Array(index, length, callback, onerror) {
            var reader = new FileReader();
            reader.onload = function(e) {
                callback(new Uint8Array(e.target.result));
            };
            reader.onerror = onerror;
            reader.readAsArrayBuffer(blobSlice(blob, index, length));
        }

        that.size = 0;
        that.init = init;
        that.readUint8Array = readUint8Array;
    }
    BlobReader.prototype = new Reader();
    BlobReader.prototype.constructor = BlobReader;

    function HttpReader(url) {
        var that = this;

        function getData(callback, onerror) {
            var request;
            if (!that.data) {
                request = new XMLHttpRequest();
                request.addEventListener("load", function() {
                    if (!that.size)
                        that.size = Number(request.getResponseHeader("Content-Length"));
                    that.data = new Uint8Array(request.response);
                    callback();
                }, false);
                request.addEventListener("error", onerror, false);
                request.open("GET", url);
                request.responseType = "arraybuffer";
                request.send();
            } else
                callback();
        }

        function init(callback, onerror) {
            var request = new XMLHttpRequest();
            request.addEventListener("load", function() {
                that.size = Number(request.getResponseHeader("Content-Length"));
                callback();
            }, false);
            request.addEventListener("error", onerror, false);
            request.open("HEAD", url);
            request.send();
        }

        function readUint8Array(index, length, callback, onerror) {
            getData(function() {
                callback(new Uint8Array(that.data.subarray(index, index + length)));
            }, onerror);
        }

        that.size = 0;
        that.init = init;
        that.readUint8Array = readUint8Array;
    }
    HttpReader.prototype = new Reader();
    HttpReader.prototype.constructor = HttpReader;

    function HttpRangeReader(url) {
        var that = this;

        function init(callback, onerror) {
            var request = new XMLHttpRequest();
            request.addEventListener("load", function() {
                that.size = Number(request.getResponseHeader("Content-Length"));
                if (request.getResponseHeader("Accept-Ranges") == "bytes")
                    callback();
                else
                    onerror(ERR_HTTP_RANGE);
            }, false);
            request.addEventListener("error", onerror, false);
            request.open("HEAD", url);
            request.send();
        }

        function readArrayBuffer(index, length, callback, onerror) {
            var request = new XMLHttpRequest();
            request.open("GET", url);
            request.responseType = "arraybuffer";
            request.setRequestHeader("Range", "bytes=" + index + "-" + (index + length - 1));
            request.addEventListener("load", function() {
                callback(request.response);
            }, false);
            request.addEventListener("error", onerror, false);
            request.send();
        }

        function readUint8Array(index, length, callback, onerror) {
            readArrayBuffer(index, length, function(arraybuffer) {
                callback(new Uint8Array(arraybuffer));
            }, onerror);
        }

        that.size = 0;
        that.init = init;
        that.readUint8Array = readUint8Array;
    }
    HttpRangeReader.prototype = new Reader();
    HttpRangeReader.prototype.constructor = HttpRangeReader;

    // Writers

    function Writer() {
    }
    Writer.prototype.getData = function(callback) {
        callback(this.data);
    };

    function TextWriter() {
        var that = this, blob;

        function init(callback) {
            blob = new Blob([], {
                type : "text/plain"
            });
            callback();
        }

        function writeUint8Array(array, callback) {
            blob = new Blob([ blob, appendABViewSupported ? array : array.buffer ], {
                type : "text/plain"
            });
            callback();
        }

        function getData(callback, onerror) {
            var reader = new FileReader();
            reader.onload = function(e) {
                callback(e.target.result);
            };
            reader.onerror = onerror;
            reader.readAsText(blob);
        }

        that.init = init;
        that.writeUint8Array = writeUint8Array;
        that.getData = getData;
    }
    TextWriter.prototype = new Writer();
    TextWriter.prototype.constructor = TextWriter;

    function Data64URIWriter(contentType) {
        var that = this, data = "", pending = "";

        function init(callback) {
            data += "data:" + (contentType || "") + ";base64,";
            callback();
        }

        function writeUint8Array(array, callback) {
            var i, delta = pending.length, dataString = pending;
            pending = "";
            for (i = 0; i < (Math.floor((delta + array.length) / 3) * 3) - delta; i++)
                dataString += String.fromCharCode(array[i]);
            for (; i < array.length; i++)
                pending += String.fromCharCode(array[i]);
            if (dataString.length > 2)
                data += obj.btoa(dataString);
            else
                pending = dataString;
            callback();
        }

        function getData(callback) {
            callback(data + obj.btoa(pending));
        }

        that.init = init;
        that.writeUint8Array = writeUint8Array;
        that.getData = getData;
    }
    Data64URIWriter.prototype = new Writer();
    Data64URIWriter.prototype.constructor = Data64URIWriter;

    function FileWriter(fileEntry, contentType) {
        var writer, that = this;

        function init(callback, onerror) {
            fileEntry.createWriter(function(fileWriter) {
                writer = fileWriter;
                callback();
            }, onerror);
        }

        function writeUint8Array(array, callback, onerror) {
            var blob = new Blob([ appendABViewSupported ? array : array.buffer ], {
                type : contentType
            });
            writer.onwrite = function() {
                writer.onwrite = null;
                callback();
            };
            writer.onerror = onerror;
            writer.write(blob);
        }

        function getData(callback) {
            fileEntry.file(callback);
        }

        that.init = init;
        that.writeUint8Array = writeUint8Array;
        that.getData = getData;
    }
    FileWriter.prototype = new Writer();
    FileWriter.prototype.constructor = FileWriter;

    function BlobWriter(contentType) {
        var blob, that = this;

        function init(callback) {
            blob = new Blob([], {
                type : contentType
            });
            callback();
        }

        function writeUint8Array(array, callback) {
            blob = new Blob([ blob, appendABViewSupported ? array : array.buffer ], {
                type : contentType
            });
            callback();
        }

        function getData(callback) {
            callback(blob);
        }

        that.init = init;
        that.writeUint8Array = writeUint8Array;
        that.getData = getData;
    }
    BlobWriter.prototype = new Writer();
    BlobWriter.prototype.constructor = BlobWriter;

    // inflate/deflate core functions

    function launchWorkerProcess(worker, reader, writer, offset, size, onappend, onprogress, onend, onreaderror, onwriteerror) {
        var chunkIndex = 0, index, outputSize;

        function onflush() {
            worker.removeEventListener("message", onmessage, false);
            onend(outputSize);
        }

        function onmessage(event) {
            var message = event.data, data = message.data;

            if (message.onappend) {
                outputSize += data.length;
                writer.writeUint8Array(data, function() {
                    onappend(false, data);
                    step();
                }, onwriteerror);
            }
            if (message.onflush)
                if (data) {
                    outputSize += data.length;
                    writer.writeUint8Array(data, function() {
                        onappend(false, data);
                        onflush();
                    }, onwriteerror);
                } else
                    onflush();
            if (message.progress && onprogress)
                onprogress(index + message.current, size);
        }

        function step() {
            index = chunkIndex * CHUNK_SIZE;
            if (index < size)
                reader.readUint8Array(offset + index, Math.min(CHUNK_SIZE, size - index), function(array) {
                    worker.postMessage({
                        append : true,
                        data : array
                    });
                    chunkIndex++;
                    if (onprogress)
                        onprogress(index, size);
                    onappend(true, array);
                }, onreaderror);
            else
                worker.postMessage({
                    flush : true
                });
        }

        outputSize = 0;
        worker.addEventListener("message", onmessage, false);
        step();
    }

    function launchProcess(process, reader, writer, offset, size, onappend, onprogress, onend, onreaderror, onwriteerror) {
        var chunkIndex = 0, index, outputSize = 0;

        function step() {
            var outputData;
            index = chunkIndex * CHUNK_SIZE;
            if (index < size)
                reader.readUint8Array(offset + index, Math.min(CHUNK_SIZE, size - index), function(inputData) {
                    var outputData = process.append(inputData, function() {
                        if (onprogress)
                            onprogress(offset + index, size);
                    });
                    outputSize += outputData.length;
                    onappend(true, inputData);
                    writer.writeUint8Array(outputData, function() {
                        onappend(false, outputData);
                        chunkIndex++;
                        setTimeout(step, 1);
                    }, onwriteerror);
                    if (onprogress)
                        onprogress(index, size);
                }, onreaderror);
            else {
                outputData = process.flush();
                if (outputData) {
                    outputSize += outputData.length;
                    writer.writeUint8Array(outputData, function() {
                        onappend(false, outputData);
                        onend(outputSize);
                    }, onwriteerror);
                } else
                    onend(outputSize);
            }
        }

        step();
    }

    function inflate(reader, writer, offset, size, computeCrc32, onend, onprogress, onreaderror, onwriteerror) {
        var worker, crc32 = new Crc32();

        function oninflateappend(sending, array) {
            if (computeCrc32 && !sending)
                crc32.append(array);
        }

        function oninflateend(outputSize) {
            onend(outputSize, crc32.get());
        }

        if (obj.zip.useWebWorkers) {
            worker = new Worker(obj.zip.inflateJSPath || INFLATE_JS);//INFLATE_JS
            launchWorkerProcess(worker, reader, writer, offset, size, oninflateappend, onprogress, oninflateend, onreaderror, onwriteerror);
        } else
            launchProcess(new obj.zip.Inflater(), reader, writer, offset, size, oninflateappend, onprogress, oninflateend, onreaderror, onwriteerror);
        return worker;
    }

    function deflate(reader, writer, level, onend, onprogress, onreaderror, onwriteerror) {
        var worker, crc32 = new Crc32();

        function ondeflateappend(sending, array) {
            if (sending)
                crc32.append(array);
        }

        function ondeflateend(outputSize) {
            onend(outputSize, crc32.get());
        }

        function onmessage() {
            worker.removeEventListener("message", onmessage, false);
            launchWorkerProcess(worker, reader, writer, 0, reader.size, ondeflateappend, onprogress, ondeflateend, onreaderror, onwriteerror);
        }

        if (obj.zip.useWebWorkers) {
            worker = new Worker(obj.zip.workerScriptsPath + DEFLATE_JS);
            worker.addEventListener("message", onmessage, false);
            worker.postMessage({
                init : true,
                level : level
            });
        } else
            launchProcess(new obj.zip.Deflater(), reader, writer, 0, reader.size, ondeflateappend, onprogress, ondeflateend, onreaderror, onwriteerror);
        return worker;
    }

    function copy(reader, writer, offset, size, computeCrc32, onend, onprogress, onreaderror, onwriteerror) {
        var chunkIndex = 0, crc32 = new Crc32();

        function step() {
            var index = chunkIndex * CHUNK_SIZE;
            if (index < size)
                reader.readUint8Array(offset + index, Math.min(CHUNK_SIZE, size - index), function(array) {
                    if (computeCrc32)
                        crc32.append(array);
                    if (onprogress)
                        onprogress(index, size, array);
                    writer.writeUint8Array(array, function() {
                        chunkIndex++;
                        step();
                    }, onwriteerror);
                }, onreaderror);
            else
                onend(size, crc32.get());
        }

        step();
    }

    // ZipReader

    function decodeASCII(str) {
        var i, out = "", charCode, extendedASCII = [ '\u00C7', '\u00FC', '\u00E9', '\u00E2', '\u00E4', '\u00E0', '\u00E5', '\u00E7', '\u00EA', '\u00EB',
            '\u00E8', '\u00EF', '\u00EE', '\u00EC', '\u00C4', '\u00C5', '\u00C9', '\u00E6', '\u00C6', '\u00F4', '\u00F6', '\u00F2', '\u00FB', '\u00F9',
            '\u00FF', '\u00D6', '\u00DC', '\u00F8', '\u00A3', '\u00D8', '\u00D7', '\u0192', '\u00E1', '\u00ED', '\u00F3', '\u00FA', '\u00F1', '\u00D1',
            '\u00AA', '\u00BA', '\u00BF', '\u00AE', '\u00AC', '\u00BD', '\u00BC', '\u00A1', '\u00AB', '\u00BB', '_', '_', '_', '\u00A6', '\u00A6',
            '\u00C1', '\u00C2', '\u00C0', '\u00A9', '\u00A6', '\u00A6', '+', '+', '\u00A2', '\u00A5', '+', '+', '-', '-', '+', '-', '+', '\u00E3',
            '\u00C3', '+', '+', '-', '-', '\u00A6', '-', '+', '\u00A4', '\u00F0', '\u00D0', '\u00CA', '\u00CB', '\u00C8', 'i', '\u00CD', '\u00CE',
            '\u00CF', '+', '+', '_', '_', '\u00A6', '\u00CC', '_', '\u00D3', '\u00DF', '\u00D4', '\u00D2', '\u00F5', '\u00D5', '\u00B5', '\u00FE',
            '\u00DE', '\u00DA', '\u00DB', '\u00D9', '\u00FD', '\u00DD', '\u00AF', '\u00B4', '\u00AD', '\u00B1', '_', '\u00BE', '\u00B6', '\u00A7',
            '\u00F7', '\u00B8', '\u00B0', '\u00A8', '\u00B7', '\u00B9', '\u00B3', '\u00B2', '_', ' ' ];
        for (i = 0; i < str.length; i++) {
            charCode = str.charCodeAt(i) & 0xFF;
            if (charCode > 127)
                out += extendedASCII[charCode - 128];
            else
                out += String.fromCharCode(charCode);
        }
        return out;
    }

    function decodeUTF8(str_data) {
        var tmp_arr = [], i = 0, ac = 0, c1 = 0, c2 = 0, c3 = 0;

        str_data += '';

        while (i < str_data.length) {
            c1 = str_data.charCodeAt(i);
            if (c1 < 128) {
                tmp_arr[ac++] = String.fromCharCode(c1);
                i++;
            } else if (c1 > 191 && c1 < 224) {
                c2 = str_data.charCodeAt(i + 1);
                tmp_arr[ac++] = String.fromCharCode(((c1 & 31) << 6) | (c2 & 63));
                i += 2;
            } else {
                c2 = str_data.charCodeAt(i + 1);
                c3 = str_data.charCodeAt(i + 2);
                tmp_arr[ac++] = String.fromCharCode(((c1 & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
                i += 3;
            }
        }

        return tmp_arr.join('');
    }

    function getString(bytes) {
        var i, str = "";
        for (i = 0; i < bytes.length; i++)
            str += String.fromCharCode(bytes[i]);
        return str;
    }

    function getDate(timeRaw) {
        var date = (timeRaw & 0xffff0000) >> 16, time = timeRaw & 0x0000ffff;
        try {
            return new Date(1980 + ((date & 0xFE00) >> 9), ((date & 0x01E0) >> 5) - 1, date & 0x001F, (time & 0xF800) >> 11, (time & 0x07E0) >> 5,
                (time & 0x001F) * 2, 0);
        } catch (e) {
        }
    }

    function readCommonHeader(entry, data, index, centralDirectory, onerror) {
        entry.version = data.view.getUint16(index, true);
        entry.bitFlag = data.view.getUint16(index + 2, true);
        entry.compressionMethod = data.view.getUint16(index + 4, true);
        entry.lastModDateRaw = data.view.getUint32(index + 6, true);
        entry.lastModDate = getDate(entry.lastModDateRaw);
        if ((entry.bitFlag & 0x01) === 0x01) {
            onerror(ERR_ENCRYPTED);
            return;
        }
        if (centralDirectory || (entry.bitFlag & 0x0008) != 0x0008) {
            entry.crc32 = data.view.getUint32(index + 10, true);
            entry.compressedSize = data.view.getUint32(index + 14, true);
            entry.uncompressedSize = data.view.getUint32(index + 18, true);
        }
        if (entry.compressedSize === 0xFFFFFFFF || entry.uncompressedSize === 0xFFFFFFFF) {
            onerror(ERR_ZIP64);
            return;
        }
        entry.filenameLength = data.view.getUint16(index + 22, true);
        entry.extraFieldLength = data.view.getUint16(index + 24, true);
    }

    function createZipReader(reader, onerror) {
        function Entry() {
        }

        Entry.prototype.getData = function(writer, onend, onprogress, checkCrc32) {
            var that = this, worker;

            function terminate(callback, param) {
                if (worker)
                    worker.terminate();
                worker = null;
                if (callback)
                    callback(param);
            }

            function testCrc32(crc32) {
                var dataCrc32 = getDataHelper(4);
                dataCrc32.view.setUint32(0, crc32);
                return that.crc32 == dataCrc32.view.getUint32(0);
            }

            function getWriterData(uncompressedSize, crc32) {
                if (checkCrc32 && !testCrc32(crc32))
                    onreaderror();
                else
                    writer.getData(function(data) {
                        terminate(onend, data);
                    });
            }

            function onreaderror() {
                terminate(onerror, ERR_READ_DATA);
            }

            function onwriteerror() {
                terminate(onerror, ERR_WRITE_DATA);
            }

            reader.readUint8Array(that.offset, 30, function(bytes) {
                var data = getDataHelper(bytes.length, bytes), dataOffset;
                if (data.view.getUint32(0) != 0x504b0304) {
                    onerror(ERR_BAD_FORMAT);
                    return;
                }
                readCommonHeader(that, data, 4, false, function(error) {
                    onerror(error);
                    return;
                });
                dataOffset = that.offset + 30 + that.filenameLength + that.extraFieldLength;
                writer.init(function() {
                    if (that.compressionMethod === 0)
                        copy(reader, writer, dataOffset, that.compressedSize, checkCrc32, getWriterData, onprogress, onreaderror, onwriteerror);
                    else
                        worker = inflate(reader, writer, dataOffset, that.compressedSize, checkCrc32, getWriterData, onprogress, onreaderror, onwriteerror);
                }, onwriteerror);
            }, onreaderror);
        };

        function seekEOCDR(offset, entriesCallback) {
            reader.readUint8Array(reader.size - offset, offset, function(bytes) {
                var dataView = getDataHelper(bytes.length, bytes).view;
                try{
                    if (dataView.getUint32(0) != 0x504b0506) {
                        seekEOCDR(offset + 1, entriesCallback);
                    } else {
                        entriesCallback(dataView);
                    }
                }catch(e){
                    onerror(ERR_READ);
                }

            }, function() {
                onerror(ERR_READ);
            });
        }

        return {
            getEntries : function(callback) {
                if (reader.size < 22) {
                    onerror(ERR_BAD_FORMAT);
                    return;
                }
                // look for End of central directory record
                seekEOCDR(22, function(dataView) {
                    var datalength, fileslength;
                    datalength = dataView.getUint32(16, true);
                    fileslength = dataView.getUint16(8, true);
                    reader.readUint8Array(datalength, reader.size - datalength, function(bytes) {
                        var i, index = 0, entries = [], entry, filename, comment, data = getDataHelper(bytes.length, bytes);
                        for (i = 0; i < fileslength; i++) {
                            entry = new Entry();
                            if (data.view.getUint32(index) != 0x504b0102) {
                                onerror(ERR_BAD_FORMAT);
                                return;
                            }
                            readCommonHeader(entry, data, index + 6, true, function(error) {
                                onerror(error);
                                return;
                            });
                            entry.commentLength = data.view.getUint16(index + 32, true);
                            entry.directory = ((data.view.getUint8(index + 38) & 0x10) == 0x10);
                            entry.offset = data.view.getUint32(index + 42, true);
                            filename = getString(data.array.subarray(index + 46, index + 46 + entry.filenameLength));
                            entry.filename = ((entry.bitFlag & 0x0800) === 0x0800) ? decodeUTF8(filename) : decodeASCII(filename);
                            if (!entry.directory && entry.filename.charAt(entry.filename.length - 1) == "/")
                                entry.directory = true;
                            comment = getString(data.array.subarray(index + 46 + entry.filenameLength + entry.extraFieldLength, index + 46
                                + entry.filenameLength + entry.extraFieldLength + entry.commentLength));
                            entry.comment = ((entry.bitFlag & 0x0800) === 0x0800) ? decodeUTF8(comment) : decodeASCII(comment);
                            entries.push(entry);
                            index += 46 + entry.filenameLength + entry.extraFieldLength + entry.commentLength;
                        }
                        callback(entries);
                    }, function() {
                        onerror(ERR_READ);
                    });
                });
            },
            close : function(callback) {
                if (callback)
                    callback();
            }
        };
    }

    // ZipWriter

    function encodeUTF8(string) {
        var n, c1, enc, utftext = [], start = 0, end = 0, stringl = string.length;
        for (n = 0; n < stringl; n++) {
            c1 = string.charCodeAt(n);
            enc = null;
            if (c1 < 128)
                end++;
            else if (c1 > 127 && c1 < 2048)
                enc = String.fromCharCode((c1 >> 6) | 192) + String.fromCharCode((c1 & 63) | 128);
            else
                enc = String.fromCharCode((c1 >> 12) | 224) + String.fromCharCode(((c1 >> 6) & 63) | 128) + String.fromCharCode((c1 & 63) | 128);
            if (enc != null) {
                if (end > start)
                    utftext += string.slice(start, end);
                utftext += enc;
                start = end = n + 1;
            }
        }
        if (end > start)
            utftext += string.slice(start, stringl);
        return utftext;
    }

    function getBytes(str) {
        var i, array = [];
        for (i = 0; i < str.length; i++)
            array.push(str.charCodeAt(i));
        return array;
    }

    function createZipWriter(writer, onerror, dontDeflate) {
        var worker, files = [], filenames = [], datalength = 0;

        function terminate(callback, message) {
            if (worker)
                worker.terminate();
            worker = null;
            if (callback)
                callback(message);
        }

        function onwriteerror() {
            terminate(onerror, ERR_WRITE);
        }

        function onreaderror() {
            terminate(onerror, ERR_READ_DATA);
        }

        return {
            add : function(name, reader, onend, onprogress, options) {
                var header, filename, date;

                function writeHeader(callback) {
                    var data;
                    date = options.lastModDate || new Date();
                    header = getDataHelper(26);
                    files[name] = {
                        headerArray : header.array,
                        directory : options.directory,
                        filename : filename,
                        offset : datalength,
                        comment : getBytes(encodeUTF8(options.comment || ""))
                    };
                    header.view.setUint32(0, 0x14000808);
                    if (options.version)
                        header.view.setUint8(0, options.version);
                    if (!dontDeflate && options.level != 0 && !options.directory)
                        header.view.setUint16(4, 0x0800);
                    header.view.setUint16(6, (((date.getHours() << 6) | date.getMinutes()) << 5) | date.getSeconds() / 2, true);
                    header.view.setUint16(8, ((((date.getFullYear() - 1980) << 4) | (date.getMonth() + 1)) << 5) | date.getDate(), true);
                    header.view.setUint16(22, filename.length, true);
                    data = getDataHelper(30 + filename.length);
                    data.view.setUint32(0, 0x504b0304);
                    data.array.set(header.array, 4);
                    data.array.set(filename, 30);
                    datalength += data.array.length;
                    writer.writeUint8Array(data.array, callback, onwriteerror);
                }

                function writeFooter(compressedLength, crc32) {
                    var footer = getDataHelper(16);
                    datalength += compressedLength || 0;
                    footer.view.setUint32(0, 0x504b0708);
                    if (typeof crc32 != "undefined") {
                        header.view.setUint32(10, crc32, true);
                        footer.view.setUint32(4, crc32, true);
                    }
                    if (reader) {
                        footer.view.setUint32(8, compressedLength, true);
                        header.view.setUint32(14, compressedLength, true);
                        footer.view.setUint32(12, reader.size, true);
                        header.view.setUint32(18, reader.size, true);
                    }
                    writer.writeUint8Array(footer.array, function() {
                        datalength += 16;
                        terminate(onend);
                    }, onwriteerror);
                }

                function writeFile() {
                    options = options || {};
                    name = name.trim();
                    if (options.directory && name.charAt(name.length - 1) != "/")
                        name += "/";
                    if (files[name])
                        throw ERR_DUPLICATED_NAME;
                    filename = getBytes(encodeUTF8(name));
                    filenames.push(name);
                    writeHeader(function() {
                        if (reader)
                            if (dontDeflate || options.level == 0)
                                copy(reader, writer, 0, reader.size, true, writeFooter, onprogress, onreaderror, onwriteerror);
                            else
                                worker = deflate(reader, writer, options.level, writeFooter, onprogress, onreaderror, onwriteerror);
                        else
                            writeFooter();
                    }, onwriteerror);
                }

                if (reader)
                    reader.init(writeFile, onreaderror);
                else
                    writeFile();
            },
            close : function(callback) {
                var data, length = 0, index = 0;
                filenames.forEach(function(name) {
                    var file = files[name];
                    length += 46 + file.filename.length + file.comment.length;
                });
                data = getDataHelper(length + 22);
                filenames.forEach(function(name) {
                    var file = files[name];
                    data.view.setUint32(index, 0x504b0102);
                    data.view.setUint16(index + 4, 0x1400);
                    data.array.set(file.headerArray, index + 6);
                    data.view.setUint16(index + 32, file.comment.length, true);
                    if (file.directory)
                        data.view.setUint8(index + 38, 0x10);
                    data.view.setUint32(index + 42, file.offset, true);
                    data.array.set(file.filename, index + 46);
                    data.array.set(file.comment, index + 46 + file.filename.length);
                    index += 46 + file.filename.length + file.comment.length;
                });
                data.view.setUint32(index, 0x504b0506);
                data.view.setUint16(index + 8, filenames.length, true);
                data.view.setUint16(index + 10, filenames.length, true);
                data.view.setUint32(index + 12, length, true);
                data.view.setUint32(index + 16, datalength, true);
                writer.writeUint8Array(data.array, function() {
                    terminate(function() {
                        writer.getData(callback);
                    });
                }, onwriteerror);
            }
        };
    }

    obj.zip = {
        Reader : Reader,
        Writer : Writer,
        BlobReader : BlobReader,
        HttpReader : HttpReader,
        HttpRangeReader : HttpRangeReader,
        Data64URIReader : Data64URIReader,
        TextReader : TextReader,
        BlobWriter : BlobWriter,
        FileWriter : FileWriter,
        Data64URIWriter : Data64URIWriter,
        TextWriter : TextWriter,
        createReader : function(reader, callback, onerror) {
            reader.init(function() {
                callback(createZipReader(reader, onerror));
            }, onerror);
        },
        createWriter : function(writer, callback, onerror, dontDeflate) {
            writer.init(function() {
                callback(createZipWriter(writer, onerror, dontDeflate));
            }, onerror);
        },
        workerScriptsPath : "public/lib/inflate.js",
        inflateJSPath : "public/lib/inflate.js",
        useWebWorkers : true
    };

})(this);

/*
 ### jQuery XML to JSON Plugin v1.3 - 2013-02-18 ###
 * http://www.fyneworks.com/ - diego@fyneworks.com
 * Licensed under http://en.wikipedia.org/wiki/MIT_License
 ###
 Website: http://www.fyneworks.com/jquery/xml-to-json/
 *//*
 # INSPIRED BY: http://www.terracoder.com/
 AND: http://www.thomasfrank.se/xml_to_json.html
 AND: http://www.kawa.net/works/js/xml/objtree-e.html
 *//*
 This simple script converts XML (document of code) into a JSON object. It is the combination of 2
 'xml to json' great parsers (see below) which allows for both 'simple' and 'extended' parsing modes.
 */
// Avoid collisions
;if(window.jQuery) (function($){

    // Add function to jQuery namespace
    $.extend({

        // converts xml documents and xml text to json object
        xml2json: function(xml, extended) {
            if(!xml) return {}; // quick fail

            //### PARSER LIBRARY
            // Core function
            function parseXML(node, simple){
                if(!node) return null;
                var txt = '', obj = null, att = null;
                var nt = node.nodeType, nn = jsVar(node.localName || node.nodeName);
                var nv = node.text || node.nodeValue || '';
                /*DBG*/ //if(window.console) console.log(['x2j',nn,nt,nv.length+' bytes']);
                if(node.childNodes){
                    if(node.childNodes.length>0){
                        /*DBG*/ //if(window.console) console.log(['x2j',nn,'CHILDREN',node.childNodes]);
                        $.each(node.childNodes, function(n,cn){
                            var cnt = cn.nodeType, cnn = jsVar(cn.localName || cn.nodeName);
                            var cnv = cn.text || cn.nodeValue || '';
                            /*DBG*/ //if(window.console) console.log(['x2j',nn,'node>a',cnn,cnt,cnv]);
                            if(cnt == 8){
                                /*DBG*/ //if(window.console) console.log(['x2j',nn,'node>b',cnn,'COMMENT (ignore)']);
                                return; // ignore comment node
                            }
                            else if(cnt == 3 || cnt == 4 || !cnn){
                                // ignore white-space in between tags
                                if(cnv.match(/^\s+$/)){
                                    /*DBG*/ //if(window.console) console.log(['x2j',nn,'node>c',cnn,'WHITE-SPACE (ignore)']);
                                    return;
                                };
                                /*DBG*/ //if(window.console) console.log(['x2j',nn,'node>d',cnn,'TEXT']);
                                txt += cnv.replace(/^\s+/,'').replace(/\s+$/,'');
                                // make sure we ditch trailing spaces from markup
                            }
                            else{
                                /*DBG*/ //if(window.console) console.log(['x2j',nn,'node>e',cnn,'OBJECT']);
                                obj = obj || {};
                                if(obj[cnn]){
                                    /*DBG*/ //if(window.console) console.log(['x2j',nn,'node>f',cnn,'ARRAY']);

                                    // http://forum.jquery.com/topic/jquery-jquery-xml2json-problems-when-siblings-of-the-same-tagname-only-have-a-textnode-as-a-child
                                    if(!obj[cnn].length) obj[cnn] = myArr(obj[cnn]);
                                    obj[cnn] = myArr(obj[cnn]);

                                    obj[cnn][ obj[cnn].length ] = parseXML(cn, true/* simple */);
                                    obj[cnn].length = obj[cnn].length;
                                }
                                else{
                                    /*DBG*/ //if(window.console) console.log(['x2j',nn,'node>g',cnn,'dig deeper...']);
                                    obj[cnn] = parseXML(cn);
                                };
                            };
                        });
                    };//node.childNodes.length>0
                };//node.childNodes
                if(node.attributes){
                    if(node.attributes.length>0){
                        /*DBG*/ //if(window.console) console.log(['x2j',nn,'ATTRIBUTES',node.attributes])
                        att = {}; obj = obj || {};
                        $.each(node.attributes, function(a,at){
                            var atn = jsVar(at.name), atv = at.value;
                            att[atn] = atv;
                            if(obj[atn]){
                                /*DBG*/ //if(window.console) console.log(['x2j',nn,'attr>',atn,'ARRAY']);

                                // http://forum.jquery.com/topic/jquery-jquery-xml2json-problems-when-siblings-of-the-same-tagname-only-have-a-textnode-as-a-child
                                //if(!obj[atn].length) obj[atn] = myArr(obj[atn]);//[ obj[ atn ] ];
                                obj[cnn] = myArr(obj[cnn]);

                                obj[atn][ obj[atn].length ] = atv;
                                obj[atn].length = obj[atn].length;
                            }
                            else{
                                /*DBG*/ //if(window.console) console.log(['x2j',nn,'attr>',atn,'TEXT']);
                                obj[atn] = atv;
                            };
                        });
                        //obj['attributes'] = att;
                    };//node.attributes.length>0
                };//node.attributes
                if(obj){
                    obj = $.extend( (txt!='' ? new String(txt) : {}),/* {text:txt},*/ obj || {}/*, att || {}*/);
                    //txt = (obj.text) ? (typeof(obj.text)=='object' ? obj.text : [obj.text || '']).concat([txt]) : txt;
                    txt = (obj.text) ? ([obj.text || '']).concat([txt]) : txt;
                    if(txt) obj.text = txt;
                    txt = '';
                };
                var out = obj || txt;
                //console.log([extended, simple, out]);
                if(extended){
                    if(txt) out = {};//new String(out);
                    txt = out.text || txt || '';
                    if(txt) out.text = txt;
                    if(!simple) out = myArr(out);
                };
                return out;
            };// parseXML
            // Core Function End
            // Utility functions
            var jsVar = function(s){ return String(s || '').replace(/-/g,"_"); };

            // NEW isNum function: 01/09/2010
            // Thanks to Emile Grau, GigaTecnologies S.L., www.gigatransfer.com, www.mygigamail.com
            function isNum(s){
                // based on utility function isNum from xml2json plugin (http://www.fyneworks.com/ - diego@fyneworks.com)
                // few bugs corrected from original function :
                // - syntax error : regexp.test(string) instead of string.test(reg)
                // - regexp modified to accept  comma as decimal mark (latin syntax : 25,24 )
                // - regexp modified to reject if no number before decimal mark  : ".7" is not accepted
                // - string is "trimmed", allowing to accept space at the beginning and end of string
                var regexp=/^((-)?([0-9]+)(([\.\,]{0,1})([0-9]+))?$)/
                return (typeof s == "number") || regexp.test(String((s && typeof s == "string") ? jQuery.trim(s) : ''));
            };
            // OLD isNum function: (for reference only)
            //var isNum = function(s){ return (typeof s == "number") || String((s && typeof s == "string") ? s : '').test(/^((-)?([0-9]*)((\.{0,1})([0-9]+))?$)/); };

            var myArr = function(o){

                // http://forum.jquery.com/topic/jquery-jquery-xml2json-problems-when-siblings-of-the-same-tagname-only-have-a-textnode-as-a-child
                //if(!o.length) o = [ o ]; o.length=o.length;
                if(!$.isArray(o)) o = [ o ]; o.length=o.length;

                // here is where you can attach additional functionality, such as searching and sorting...
                return o;
            };
            // Utility functions End
            //### PARSER LIBRARY END

            // Convert plain text to xml
            if(typeof xml=='string') xml = $.text2xml(xml);

            // Quick fail if not xml (or if this is a node)
            if(!xml.nodeType) return;
            if(xml.nodeType == 3 || xml.nodeType == 4) return xml.nodeValue;

            // Find xml root node
            var root = (xml.nodeType == 9) ? xml.documentElement : xml;

            // Convert xml to json
            var out = parseXML(root, true /* simple */);

            // Clean-up memory
            xml = null; root = null;

            // Send output
            return out;
        },

        // Convert text to XML DOM
        text2xml: function(str) {
            // NOTE: I'd like to use jQuery for this, but jQuery makes all tags uppercase
            //return $(xml)[0];

            /* prior to jquery 1.9 */
            /*
             var out;
             try{
             var xml = ((!$.support.opacity && !$.support.style))?new ActiveXObject("Microsoft.XMLDOM"):new DOMParser();
             xml.async = false;
             }catch(e){ throw new Error("XML Parser could not be instantiated") };
             try{
             if((!$.support.opacity && !$.support.style)) out = (xml.loadXML(str))?xml:false;
             else out = xml.parseFromString(str, "text/xml");
             }catch(e){ throw new Error("Error parsing XML string") };
             return out;
             */

            /* jquery 1.9+ */
            return $.parseXML(str);
        }

    }); // extend $

})(jQuery);
/**
 * @fileOverview FreeMind 文件格式支持
 *
 * Freemind 文件后缀为 .mm，实际上是一个 XML 文件
 * @see http://freemind.sourceforge.net/
 */

window.kityminder.data.registerProtocol('freemind', (function(minder) {

    var Promise = kityminder.Promise;
    // 标签 map
    var markerMap = {
        'full-1': ['priority', 1],
        'full-2': ['priority', 2],
        'full-3': ['priority', 3],
        'full-4': ['priority', 4],
        'full-5': ['priority', 5],
        'full-6': ['priority', 6],
        'full-7': ['priority', 7],
        'full-8': ['priority', 8]
    };

    function processTopic(topic, obj) {

        //处理文本
        obj.data = {
            text: topic.TEXT
        };
        var i;

        // 处理标签
        if (topic.icon) {
            var icons = topic.icon;
            var type;
            if (icons.length && icons.length > 0) {
                for (i in icons) {
                    type = markerMap[icons[i].BUILTIN];
                    if (type) obj.data[type[0]] = type[1];
                }
            } else {
                type = markerMap[icons.BUILTIN];
                if (type) obj.data[type[0]] = type[1];
            }
        }

        // 处理超链接
        if (topic.LINK) {
            obj.data.hyperlink = topic.LINK;
        }

        //处理子节点
        if (topic.node) {

            var tmp = topic.node;
            if (tmp.length && tmp.length > 0) { //多个子节点
                obj.children = [];

                for (i in tmp) {
                    obj.children.push({});
                    processTopic(tmp[i], obj.children[i]);
                }

            } else { //一个子节点
                obj.children = [{}];
                processTopic(tmp, obj.children[0]);
            }
        }
    }

    function xml2km(xml) {
        var json = $.xml2json(xml);
        var result = {};
        processTopic(json.node, result);
        return result;
    }

    return {
        fileDescription: 'Freemind 格式',
        fileExtension: '.mm',
        dataType: 'text',

        decode: function(local) {
            return new Promise(function(resolve, reject) {
                try {
                    resolve(xml2km(local));
                } catch (e) {
                    reject(new Error('XML 文件损坏！'));
                }
            });
        },

        encode: function(json, km, options) {
            var url = 'home/export';
            var data = JSON.stringify(json.root);

            function fetch() {
                return new Promise(function(resolve, reject) {
                    var xhr = new XMLHttpRequest();
                    xhr.open('POST', url);

                    xhr.responseType = 'blob';
                    xhr.onload = resolve;
                    xhr.onerror = reject;

                    var form = new FormData();
                    form.append('type', 'freemind');
                    form.append('data', data);
                    form.append('csrf_token', document.querySelector('#km-csrf').value);

                    xhr.send(form);
                }).then(function(e) {
                        return e.target.response;
                    });
            }

            function download() {
                var filename = options.filename || 'freemind.mm';

                var form = document.createElement('form');
                form.setAttribute('action', url);
                form.setAttribute('method', 'POST');
                form.appendChild(field('filename', filename));
                form.appendChild(field('type', 'freemind'));
                form.appendChild(field('data', data));
                form.appendChild(field('download', '1'));
                form.appendChild(field('csrf_token', document.querySelector('#km-csrf').value));
                document.body.appendChild(form);
                form.submit();
                document.body.removeChild(form);

                function field(name, content) {
                    var input = document.createElement('input');
                    input.type = 'hidden';
                    input.name = name;
                    input.value = content;
                    return input;
                }
            }

            if (options && options.download) {
                return download();
            } else {
                return fetch();
            }
        }
    };

}()));
/* global zip:true */
/*
 http://www.xmind.net/developer/
 Parsing XMind file
 XMind files are generated in XMind Workbook (.xmind) format, an open format
 that is based on the principles of OpenDocument. It consists of a ZIP
 compressed archive containing separate XML documents for content and styles,
 a .jpg image file for thumbnails, and directories for related attachments.
 */
window.kityminder.data.registerProtocol('xmind', (function(minder) {
    var Promise = window.kityminder.Promise;

    // 标签 map
    var markerMap = {
        'priority-1': ['priority', 1],
        'priority-2': ['priority', 2],
        'priority-3': ['priority', 3],
        'priority-4': ['priority', 4],
        'priority-5': ['priority', 5],
        'priority-6': ['priority', 6],
        'priority-7': ['priority', 7],
        'priority-8': ['priority', 8],

        'task-start': ['progress', 1],
        'task-oct': ['progress', 2],
        'task-quarter': ['progress', 3],
        'task-3oct': ['progress', 4],
        'task-half': ['progress', 5],
        'task-5oct': ['progress', 6],
        'task-3quar': ['progress', 7],
        'task-7oct': ['progress', 8],
        'task-done': ['progress', 9]
    };

    return {
        fileDescription: 'XMind 格式',
        fileExtension: '.xmind',
        dataType: 'blob',
        mineType: 'application/octet-stream',

        decode: function(local) {

            function processTopic(topic, obj) {

                //处理文本
                obj.data = {
                    text: topic.title
                };

                // 处理标签
                if (topic.marker_refs && topic.marker_refs.marker_ref) {
                    var markers = topic.marker_refs.marker_ref;
                    var type;
                    if (markers.length && markers.length > 0) {
                        for (var i in markers) {
                            type = markerMap[markers[i].marker_id];
                            if (type) obj.data[type[0]] = type[1];
                        }
                    } else {
                        type = markerMap[markers.marker_id];
                        if (type) obj.data[type[0]] = type[1];
                    }
                }

                // 处理超链接
                if (topic['xlink:href']) {
                    obj.data.hyperlink = topic['xlink:href'];
                }
                //处理子节点
                var topics = topic.children && topic.children.topics;
                var subTopics = topics && (topics.topic || topics[0] && topics[0].topic);
                if (subTopics) {
                    var tmp = subTopics;
                    if (tmp.length && tmp.length > 0) { //多个子节点
                        obj.children = [];

                        for (var i in tmp) {
                            obj.children.push({});
                            processTopic(tmp[i], obj.children[i]);
                        }

                    } else { //一个子节点
                        obj.children = [{}];
                        processTopic(tmp, obj.children[0]);
                    }
                }
            }

            function xml2km(xml) {
                var json = $.xml2json(xml);
                var result = {};
                var sheet = json.sheet;
                var topic = Array.isArray(sheet) ? sheet[0].topic : sheet.topic;
                processTopic(topic, result);
                return result;
            }

            function getEntries(file, onend) {
                return new Promise(function(resolve, reject) {
                    zip.createReader(new zip.BlobReader(file), function(zipReader) {
                        zipReader.getEntries(resolve);
                    }, reject);
                });
            }

            function readDocument(entries) {
                return new Promise(function(resolve, reject) {
                    var entry, json;

                    // 查找文档入口
                    while ((entry = entries.pop())) {

                        if (entry.filename.split('/').pop() == 'content.xml') break;

                        entry = null;

                    }

                    // 找到了读取数据
                    if (entry) {

                        entry.getData(new zip.TextWriter(), function(text) {
                            try {
                                json = xml2km($.parseXML(text));
                                resolve(json);
                            } catch (e) {
                                reject(e);
                            }
                        });

                    }

                    // 找不到返回失败
                    else {
                        reject(new Error('Content document missing'));
                    }
                });
            }

            return getEntries(local).then(readDocument);
        },

        encode: function(json, km, options) {
            var url = 'home/export';
            var data = JSON.stringify(json.root);

            function fetch() {
                return new Promise(function(resolve, reject) {

                    var xhr = new XMLHttpRequest();
                    xhr.open('POST', url);

                    xhr.responseType = 'blob';
                    xhr.onload = resolve;
                    xhr.onerror = reject;

                    var form = new FormData();
                    form.append('type', 'xmind');
                    form.append('data', data);
                    form.append('csrf_token', document.querySelector('#km-csrf').value);

                    xhr.send(form);

                }).then(function(e) {
                        return e.target.response;
                    });
            }

            function download() {
                var filename = options.filename || 'xmind.xmind';

                var form = document.createElement('form');
                form.setAttribute('action', url);
                form.setAttribute('method', 'POST');
                form.appendChild(field('filename', filename));
                form.appendChild(field('type', 'xmind'));
                form.appendChild(field('data', data));
                form.appendChild(field('download', '1'));
                form.appendChild(field('csrf_token', document.querySelector('#km-csrf').value));
                document.body.appendChild(form);
                form.submit();
                document.body.removeChild(form);

                function field(name, content) {
                    var input = document.createElement('input');
                    input.type = 'hidden';
                    input.name = name;
                    input.value = content;
                    return input;
                }
            }

            if (options && options.download) {
                return download();
            } else {
                return fetch();
            }
        },

        // recognize: recognize,
        recognizePriority: -1
    };

}()));
/* global zip:true */
/*
 http://www.mindjet.com/mindmanager/
 mindmanager的后缀为.mmap，实际文件格式是zip，解压之后核心文件是Document.xml
 */
window.kityminder.data.registerProtocol('mindmanager', (function(minder) {

    var Promise = kityminder.Promise;

    // 标签 map
    var markerMap = {
        'urn:mindjet:Prio1': ['PriorityIcon', 1],
        'urn:mindjet:Prio2': ['PriorityIcon', 2],
        'urn:mindjet:Prio3': ['PriorityIcon', 3],
        'urn:mindjet:Prio4': ['PriorityIcon', 4],
        'urn:mindjet:Prio5': ['PriorityIcon', 5],
        '0': ['ProgressIcon', 1],
        '25': ['ProgressIcon', 2],
        '50': ['ProgressIcon', 3],
        '75': ['ProgressIcon', 4],
        '100': ['ProgressIcon', 5]
    };

    function processTopic(topic, obj) {
        //处理文本
        obj.data = {
            text: topic.Text && topic.Text.PlainText || ''
        }; // 节点默认的文本，没有Text属性

        // 处理标签
        if (topic.Task) {

            var type;
            if (topic.Task.TaskPriority) {
                type = markerMap[topic.Task.TaskPriority];
                if (type) obj.data[type[0]] = type[1];
            }

            if (topic.Task.TaskPercentage) {
                type = markerMap[topic.Task.TaskPercentage];
                if (type) obj.data[type[0]] = type[1];
            }
        }

        // 处理超链接
        if (topic.Hyperlink) {
            obj.data.hyperlink = topic.Hyperlink.Url;
        }

        //处理子节点
        if (topic.SubTopics && topic.SubTopics.Topic) {

            var tmp = topic.SubTopics.Topic;
            if (tmp.length && tmp.length > 0) { //多个子节点
                obj.children = [];

                for (var i in tmp) {
                    obj.children.push({});
                    processTopic(tmp[i], obj.children[i]);
                }

            } else { //一个子节点
                obj.children = [{}];
                processTopic(tmp, obj.children[0]);
            }
        }
    }

    function xml2km(xml) {
        var json = $.xml2json(xml);
        var result = {};
        processTopic(json.OneTopic.Topic, result);
        return result;
    }

    function getEntries(file) {
        return new Promise(function(resolve, reject) {
            zip.createReader(new zip.BlobReader(file), function(zipReader) {
                zipReader.getEntries(resolve);
            }, reject);
        });
    }

    function readMainDocument(entries) {

        return new Promise(function(resolve, reject) {

            var entry, json;

            // 查找文档入口
            while ((entry = entries.pop())) {

                if (entry.filename.split('/').pop() == 'Document.xml') break;

                entry = null;

            }

            // 找到了读取数据
            if (entry) {

                entry.getData(new zip.TextWriter(), function(text) {
                    json = xml2km($.parseXML(text));
                    resolve(json);
                });

            }

            // 找不到返回失败
            else {
                reject(new Error('Main document missing'));
            }

        });
    }

    return {
        fileDescription: 'MindManager 格式',
        fileExtension: '.mmap',
        dataType: 'blob',

        decode: function(local) {
            return getEntries(local).then(readMainDocument);
        },

        // 暂时不支持编码
        encode: null,

        recognizePriority: -1
    };
}()));