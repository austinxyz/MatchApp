package com.utr.player;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerAnalyserTest {

    @Test
    void compareSingle() {
        PlayerAnalyser analyser = PlayerAnalyser.getInstance();
        System.out.println(analyser.compareSingle("1316122", "3190677"));
    }

    @Test
    void simpleTest() {
        System.out.println("18+".compareTo("40+"));
        System.out.println("18+".compareTo("55+"));
        System.out.println("40+".compareTo("40+"));
    }

    @Test
    void simpleTest1() {
        int[][] winTable = new int[][] {{0,1,1,-1,1,-1,1},
                                        {0,0,1,1,-1,-1,-1},
                {0,0,0,-1,0,-1,-1},
                {-1,0,-1,0,1,0,1},
                {0,-1,1,0,0,1,-1},
                {-1,-1,-1,1,0,0,0},
                {0,-1,-1,0,-1,1,0}
        };

        visit(winTable, 0, 0);
    }

    private void visit(int[][] winTable, int i, int j) {

        if (i == 6 && j == 6) {
            porder(winTable);
            return;
        }

        if (j == 7) {
            j = 0;
            i++;
        }

        if (winTable[i][j] == -1) {
            winTable[i][j] = 1;
            winTable[j][i] = 0;
            visit(winTable, i, j+1);
            winTable[i][j] = 0;
            winTable[j][i] = 1;
            visit(winTable, i, j+1);
            winTable[i][j] = -1;
            winTable[j][i] = -1;
        } else {
            visit(winTable, i, j+1);
        }
    }

    private void porder(int[][] winTable) {
        int count4 = 0;
        for (int i=0; i<=6;i++) {
            int sum = 0;
            for (int j=0; j<=6; j++) {
                sum+=winTable[i][j];
            }
            System.out.print(i + "-" + sum + ",");
            if (sum >=4) {
                count4++;
            }
        }
        System.out.println(": win>4:" + count4);
    }


}