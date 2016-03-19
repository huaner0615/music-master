package com.buaa.bean;

import com.buaa.utils.L;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Window10 on 2016/3/17.
 */
public class Lyric {
    private SongInfo songInfo;
    private Set<LyricLine> lyricLineSet;
    private LyricLine now;
    private LyricLine next;
    private Iterator<LyricLine> iterator;
    private Set<LyricLine> frount;
    private Set<LyricLine> after;

    public Lyric(SongInfo songInfo) {
        this.songInfo = songInfo;
        lyricLineSet = new TreeSet<>();
        frount = new TreeSet<>();
        after = new TreeSet<>();
    }

    public void addLyricLine(LyricLine lyricLine) {
        if (lyricLine != null) {
            lyricLineSet.add(lyricLine);
        }
    }

    public Set<LyricLine> getLyricLineSet() {
        return lyricLineSet;
    }

    public LyricLine getLyricLine(int nowTime) {
        if (now == null) {
            if (iterator.hasNext()) {
                now = iterator.next();
            }
            if (iterator.hasNext()) {
                next = iterator.next();
            }
            if (nowTime > now.getTime()) {
                LyricLine retured = now;
                nextLyricLine();
                return retured;
            }
            return null;
        }
        if (nowTime > now.getTime()) {
            LyricLine retured = now;
            nextLyricLine();
            return retured;
        } else {
            return null;
        }

    }

    private void nextLyricLine() {
        frount.add(now);
        after.remove(now);
        now = next;
        if (iterator.hasNext()) {
            next = iterator.next();
        }
    }

    public void commit() {

        iterator = lyricLineSet.iterator();
        while (iterator.hasNext()){
            L.i(iterator.next().getStr());
        }
        iterator = lyricLineSet.iterator();
        after.addAll(lyricLineSet);
    }

    public void seekTo(int progress) {
        iterator = lyricLineSet.iterator();
        frount = new TreeSet<>();
        after = new TreeSet<>();
        after.addAll(lyricLineSet);
        LyricLine pre = null;
        LyricLine next = null;
        while (iterator.hasNext()) {
            LyricLine current = iterator.next();
            if (pre == null) {
                pre = current;
            } else {
                pre = next;
            }
            next = current;
            frount.add(pre);
            after.remove(next);
            if (current.getTime() > progress) {
                now = pre;
                this.next = next;
                break;
            }
        }
    }

    public void log() {
        L.i("lyricLineSet:"+lyricLineSet.size()+"    frount:"+frount.size()+"   frount:"+after.size());
    }

    public Set<LyricLine> getAfter() {
        return after;
    }

    public Set<LyricLine> getFrount() {
        return frount;
    }

    public LyricLine getNow() {
        return now;
    }
}
