package xyz.rive.jttplayer.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EqualizerOptions {
    private boolean enabled = false;
    private double stereoPan = 0;
    private double surroundPan = 0;
    private double volumeGain = 0;
    private int[] customEqValues = new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    private int currentEqIndex = 1;
    public static final int CUSTOM_EQ_INDEX = 1;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public double getStereoPan() {
        return stereoPan;
    }

    public void setStereoPan(double stereoPan) {
        this.stereoPan = stereoPan;
    }

    public double getSurroundPan() {
        return surroundPan;
    }

    public void setSurroundPan(double surroundPan) {
        this.surroundPan = surroundPan;
    }

    public double getVolumeGain() {
        return volumeGain;
    }

    public void setVolumeGain(double volumeGain) {
        this.volumeGain = volumeGain;
    }

    public int[] getCustomEqValues() {
        return customEqValues;
    }

    public void setCustomEqValues(int[] customEqValues) {
        this.customEqValues = customEqValues;
    }

    public int getCurrentEqIndex() {
        return currentEqIndex;
    }

    public void setCurrentEqIndex(int currentEqIndex) {
        this.currentEqIndex = currentEqIndex;
    }

    @JsonIgnore
    public int[] getCurrentEqValues() {
        if(enabled) {
            switch (currentEqIndex) {
                case 0: //推荐配置
                    return new int[]{ 4, 2, 0, -3, -6, -6, -3, 0, 3, 5 };
                case 1: //自定义
                    return customEqValues;
                case 2: //流行音乐
                    return new int[]{ 3, 1, 0, -2, -4, -4, -2, 0, 1, 2 };
                case 3: //摇滚
                    return new int[]{ -2, 0, 2, 4, -2, -2, 0, 0, 4, 4 };
                case 4: //金属乐
                    return new int[]{ -6, 0, 0, 0, 0, 0, 4, 0, 4, 0 };
                case 5: //舞曲
                    return new int[]{ -2, 3, 4, 1, -2, -2, 0, 0, 4, 4 };
                case 6: //电子乐
                    return new int[]{ -6, 1, 4, -2, -2, -4, 0, 0, 6, 6 };
                case 7: //乡村音乐
                    return new int[]{ -2, 0, 0, 2, 2, 0, 0, 0, 4, 4 };
                case 8: //爵士乐
                    return new int[]{ 0, 0, 0, 4, 4, 4, 0, 2, 3, 4 };
                case 9: //古典
                    return new int[]{ 0, 8, 8, 4, 0, 0, 0, 0, 2, 2 };
                case 10: //布鲁斯
                    return new int[]{ -2, 0, 2, 1, 0, 0, 0, 0, -2, -4 };
                case 11: //怀旧音乐
                    return new int[]{ -4, 0, 2, 1, 0, 0, 0, 0, -4, -6 };
                case 12: //歌剧
                    return new int[]{ 0, 0, 0, 4, 5, 3, 6, 3, 0, 0 };
                case 13: //语音
                    return new int[]{ -2, 0, 2, 1, 0, 0, 0, 0, -4, -6 };
            }
        }
        return new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    }

    public void resetCustomEq() {
        Arrays.fill(customEqValues, 0);
        setCurrentEqIndex(1);
    }

}
