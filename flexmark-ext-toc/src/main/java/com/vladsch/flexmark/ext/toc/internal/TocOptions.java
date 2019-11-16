package com.vladsch.flexmark.ext.toc.internal;

import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.util.Immutable;
import com.vladsch.flexmark.util.Mutable;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.data.MutableDataSetter;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.vladsch.flexmark.util.sequence.IRichSequence.HASH_SET;

@SuppressWarnings("WeakerAccess")
public class TocOptions implements Immutable<TocOptions, TocOptions.AsMutable>, MutableDataSetter {
    public static final TocOptions DEFAULT = new TocOptions();
    public static final int DEFAULT_LEVELS = 4 | 8; // 0 not used, default H2 & H3, H1 assumed to be document heading and does not need to be part of TOC
    public static final String DEFAULT_TITLE = "Table of Contents";
    public static final int DEFAULT_TITLE_LEVEL = 1;
    public static final int VALID_LEVELS = 0x7e;
    public static final ListType LIST_TYPE = ListType.HIERARCHY;

    public enum ListType {
        HIERARCHY,
        FLAT,
        FLAT_REVERSED,
        SORTED,
        SORTED_REVERSED
    }

    public final int levels;
    public final boolean isTextOnly;
    public final boolean isNumbered;
    public final ListType listType;
    public final boolean isHtml;
    public final int titleLevel;
    public final String title;
    public final int rawTitleLevel;
    public final String rawTitle;
    public final boolean isAstAddOptions;
    public final boolean isBlankLineSpacer;
    public final String divClass;
    public final String listClass;
    public final boolean isCaseSensitiveTocTag;

    @Override
    public AsMutable toMutable() {
        return new AsMutable(this);
    }

    public TocOptions() {
        this(DEFAULT_LEVELS, false, false, false, DEFAULT_TITLE_LEVEL, DEFAULT_TITLE, ListType.HIERARCHY, false, true, "", "", true);
    }

    public TocOptions(int levels, boolean isHtml, boolean isTextOnly, boolean isNumbered, ListType listType) {
        this(levels, isHtml, isTextOnly, isNumbered, DEFAULT_TITLE_LEVEL, DEFAULT_TITLE, listType, false, true, "", "", true);
    }

    public TocOptions(int levels, boolean isHtml, boolean isTextOnly, boolean isNumbered, int titleLevel, String title, ListType listType) {
        this(levels, isHtml, isTextOnly, isNumbered, titleLevel, title, listType, false, true, "", "", true);
    }

    public TocOptions(@NotNull TocOptions.AsMutable other) {
        levels = other.levels;
        isTextOnly = other.isTextOnly;
        isNumbered = other.isNumbered;
        listType = other.listType;
        isHtml = other.isHtml;
        titleLevel = other.titleLevel;
        title = other.title;
        rawTitleLevel = other.rawTitleLevel;
        rawTitle = other.rawTitle;
        isAstAddOptions = other.isAstAddOptions;
        isBlankLineSpacer = other.isBlankLineSpacer;
        divClass = other.divClass;
        listClass = other.listClass;
        isCaseSensitiveTocTag = other.isCaseSensitiveTocTag;
    }

    public TocOptions(@NotNull TocOptions other) {
        levels = other.levels;
        isTextOnly = other.isTextOnly;
        isNumbered = other.isNumbered;
        listType = other.listType;
        isHtml = other.isHtml;
        titleLevel = other.titleLevel;
        title = other.title;
        rawTitleLevel = other.rawTitleLevel;
        rawTitle = other.rawTitle;
        isAstAddOptions = other.isAstAddOptions;
        isBlankLineSpacer = other.isBlankLineSpacer;
        divClass = other.divClass;
        listClass = other.listClass;
        isCaseSensitiveTocTag = other.isCaseSensitiveTocTag;
    }

    //public TocOptions(DataHolder options) {
    //    this(options, true);
    //}

    public TocOptions(@Nullable DataHolder options, boolean isSimToc) {
        this(
                TocExtension.LEVELS.get(options),
                TocExtension.IS_HTML.get(options),
                TocExtension.IS_TEXT_ONLY.get(options),
                TocExtension.IS_NUMBERED.get(options),
                TocExtension.TITLE_LEVEL.get(options),
                TocExtension.TITLE.get(options) == null ? (isSimToc ? DEFAULT_TITLE : "") : TocExtension.TITLE.get(options),
                TocExtension.LIST_TYPE.get(options),
                TocExtension.AST_INCLUDE_OPTIONS.get(options),
                TocExtension.BLANK_LINE_SPACER.get(options),
                TocExtension.DIV_CLASS.get(options),
                TocExtension.LIST_CLASS.get(options),
                TocExtension.CASE_SENSITIVE_TOC_TAG.get(options)
        );
    }

    @NotNull
    @Override
    public MutableDataHolder setIn(@NotNull MutableDataHolder dataHolder) {
        dataHolder.set(TocExtension.LEVELS, levels);
        dataHolder.set(TocExtension.IS_TEXT_ONLY, isTextOnly);
        dataHolder.set(TocExtension.IS_NUMBERED, isNumbered);
        dataHolder.set(TocExtension.LIST_TYPE, listType);
        dataHolder.set(TocExtension.IS_HTML, isHtml);
        dataHolder.set(TocExtension.TITLE_LEVEL, titleLevel);
        dataHolder.set(TocExtension.TITLE, title);
        dataHolder.set(TocExtension.AST_INCLUDE_OPTIONS, isAstAddOptions);
        dataHolder.set(TocExtension.BLANK_LINE_SPACER, isBlankLineSpacer);
        dataHolder.set(TocExtension.DIV_CLASS, divClass);
        dataHolder.set(TocExtension.LIST_CLASS, listClass);
        dataHolder.set(TocExtension.CASE_SENSITIVE_TOC_TAG, isCaseSensitiveTocTag);
        return dataHolder;
    }

    public TocOptions(
            int levels,
            boolean isHtml,
            boolean isTextOnly,
            boolean isNumbered,
            int titleLevel,
            CharSequence title,
            ListType listType,
            boolean isAstAddOptions,
            boolean isBlankLineSpacer,
            CharSequence divClass,
            CharSequence listClass,
            boolean isCaseSensitiveTocTag
    ) {
        this.levels = VALID_LEVELS & levels;
        this.isTextOnly = isTextOnly;
        this.isNumbered = isNumbered;
        this.listType = listType;
        this.isHtml = isHtml;
        this.rawTitle = title == null ? "" : String.valueOf(title);
        this.rawTitleLevel = titleLevel;
        if (!rawTitle.isEmpty()) {
            CharSequence charSequence = rawTitle.trim();
            int markers = BasedSequence.of(charSequence, 0, charSequence.length()).countLeading(HASH_SET);
            if (markers >= 1 && markers <= 6) titleLevel = markers;
            this.title = rawTitle.substring(markers).trim();
        } else {
            this.title = "";
        }
        this.titleLevel = titleLevel <= 1 ? 1 : titleLevel >= 6 ? 6 : titleLevel;

        this.isAstAddOptions = isAstAddOptions;
        this.isBlankLineSpacer = isBlankLineSpacer;
        this.divClass = divClass instanceof String ? (String) divClass : String.valueOf(divClass);
        this.listClass = listClass instanceof String ? (String) listClass : String.valueOf(listClass);
        this.isCaseSensitiveTocTag = isCaseSensitiveTocTag;
    }

    public boolean isLevelIncluded(int level) {
        return level >= 1 && level <= 6 && (levels & 1 << level) != 0;
    }

    // @Formatter:off
    public TocOptions withLevels(int levels)                             { return new TocOptions(levels, isHtml, isTextOnly, isNumbered, titleLevel, title, listType, isAstAddOptions, isBlankLineSpacer, divClass, listClass, isCaseSensitiveTocTag); }
    public TocOptions withIsHtml(boolean isHtml)                         { return new TocOptions(levels, isHtml, isTextOnly, isNumbered, titleLevel, title, listType, isAstAddOptions, isBlankLineSpacer, divClass, listClass, isCaseSensitiveTocTag); }
    public TocOptions withIsTextOnly(boolean isTextOnly)                 { return new TocOptions(levels, isHtml, isTextOnly, isNumbered, titleLevel, title, listType, isAstAddOptions, isBlankLineSpacer, divClass, listClass, isCaseSensitiveTocTag); }
    public TocOptions withIsNumbered(boolean isNumbered)                 { return new TocOptions(levels, isHtml, isTextOnly, isNumbered, titleLevel, title, listType, isAstAddOptions, isBlankLineSpacer, divClass, listClass, isCaseSensitiveTocTag); }
    public TocOptions withTitleLevel(int titleLevel)                     { return new TocOptions(levels, isHtml, isTextOnly, isNumbered, titleLevel, title, listType, isAstAddOptions, isBlankLineSpacer, divClass, listClass, isCaseSensitiveTocTag); }
    public TocOptions withTitle(CharSequence title)                      { return new TocOptions(levels, isHtml, isTextOnly, isNumbered, titleLevel, title, listType, isAstAddOptions, isBlankLineSpacer, divClass, listClass, isCaseSensitiveTocTag); }
    public TocOptions withListType(ListType listType)                    { return new TocOptions(levels, isHtml, isTextOnly, isNumbered, titleLevel, title, listType, isAstAddOptions, isBlankLineSpacer, divClass, listClass, isCaseSensitiveTocTag); }
    public TocOptions withIsAstAddOptions(boolean isAstAddOptions)       { return new TocOptions(levels, isHtml, isTextOnly, isNumbered, titleLevel, title, listType, isAstAddOptions, isBlankLineSpacer, divClass, listClass, isCaseSensitiveTocTag); }
    public TocOptions withIsBlankLineSpacer(boolean isBlankLineSpacer)   { return new TocOptions(levels, isHtml, isTextOnly, isNumbered, titleLevel, title, listType, isAstAddOptions, isBlankLineSpacer, divClass, listClass, isCaseSensitiveTocTag); }

    public TocOptions withRawTitleLevel(int titleLevel)                  { return new TocOptions(levels, isHtml, isTextOnly, isNumbered, titleLevel, title, listType, isAstAddOptions, isBlankLineSpacer, divClass, listClass, isCaseSensitiveTocTag); }
    public TocOptions withRawTitle(CharSequence title)                   { return new TocOptions(levels, isHtml, isTextOnly, isNumbered, titleLevel, title, listType, isAstAddOptions, isBlankLineSpacer, divClass, listClass, isCaseSensitiveTocTag); }
    public TocOptions withDivClass(CharSequence divClass)                { return new TocOptions(levels, isHtml, isTextOnly, isNumbered, titleLevel, title, listType, isAstAddOptions, isBlankLineSpacer, divClass, listClass, isCaseSensitiveTocTag); }
    public TocOptions withListClass(CharSequence listClass)              { return new TocOptions(levels, isHtml, isTextOnly, isNumbered, titleLevel, title, listType, isAstAddOptions, isBlankLineSpacer, divClass, listClass, isCaseSensitiveTocTag); }
    // @Formatter:on

    public TocOptions withLevelList(int... levelList) {
        int levels = getLevels(levelList);
        return withLevels(levels);
    }

    public static int getLevels(int... levelList) {
        int levels = 0;
        for (int level : levelList) {
            if (level < 1 || level > 6) throw new IllegalArgumentException("TocOption level out of range [1, 6]");
            levels |= 1 << level;
        }
        return levels;
    }

    public String getTitleHeading() {
        String title = this.title;

        if (!title.isEmpty()) {
            StringBuilder out = new StringBuilder();

            int level = titleLevel;
            while (level-- > 0) out.append('#');
            out.append(' ');
            out.append(title);
            return out.toString();
        }
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TocOptions || o instanceof TocOptions.AsMutable)) return false;

        TocOptions options = o instanceof TocOptions ? (TocOptions) o : ((TocOptions.AsMutable) o).toImmutable();

        if (levels != options.levels) return false;
        if (isTextOnly != options.isTextOnly) return false;
        if (isNumbered != options.isNumbered) return false;
        if (listType != options.listType) return false;
        if (isHtml != options.isHtml) return false;
        if (titleLevel != options.titleLevel) return false;
        if (!title.equals(options.title)) return false;
        if (rawTitleLevel != options.rawTitleLevel) return false;
        if (!rawTitle.equals(options.rawTitle)) return false;
        if (!divClass.equals(options.divClass)) return false;
        if (!listClass.equals(options.listClass)) return false;
        if (isAstAddOptions != options.isAstAddOptions) return false;
        if (isBlankLineSpacer != options.isBlankLineSpacer) return false;
        if (isCaseSensitiveTocTag != options.isCaseSensitiveTocTag) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = levels;
        result = 31 * result + (isTextOnly ? 1 : 0);
        result = 31 * result + (isNumbered ? 1 : 0);
        result = 31 * result + listType.hashCode();
        result = 31 * result + (isHtml ? 1 : 0);
        result = 31 * result + titleLevel;
        result = 31 * result + title.hashCode();
        result = 31 * result + rawTitleLevel;
        result = 31 * result + rawTitle.hashCode();
        result = 31 * result + divClass.hashCode();
        result = 31 * result + listClass.hashCode();
        result = 31 * result + (isAstAddOptions ? 1 : 0);
        result = 31 * result + (isBlankLineSpacer ? 1 : 0);
        result = 31 * result + (isCaseSensitiveTocTag ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TocOptions { " +
                "levels=" + levels +
                ", isHtml=" + isHtml +
                ", isTextOnly=" + isTextOnly +
                ", isNumbered=" + isNumbered +
                ", titleLevel=" + titleLevel +
                ", title='" + title + '\'' +
                ", rawTitleLevel=" + rawTitleLevel +
                ", listType=" + listType +
                ", rawTitle='" + rawTitle + '\'' +
                ", divClass='" + divClass + '\'' +
                ", listClass='" + listClass + '\'' +
                " }";
    }

    public static class AsMutable implements Mutable<AsMutable, TocOptions>, MutableDataSetter {
        public int levels;
        public boolean isTextOnly;
        public boolean isNumbered;
        public ListType listType;
        public boolean isHtml;
        public int titleLevel;
        public String title;
        public int rawTitleLevel;
        public String rawTitle;
        public boolean isAstAddOptions;
        public boolean isBlankLineSpacer;
        public String divClass;
        public String listClass;
        public boolean isCaseSensitiveTocTag;

        protected AsMutable(TocOptions other) {
            levels = other.levels;
            isTextOnly = other.isTextOnly;
            isNumbered = other.isNumbered;
            listType = other.listType;
            isHtml = other.isHtml;
            titleLevel = other.titleLevel;
            title = other.title;
            rawTitleLevel = other.rawTitleLevel;
            rawTitle = other.rawTitle;
            isAstAddOptions = other.isAstAddOptions;
            isBlankLineSpacer = other.isBlankLineSpacer;
            divClass = other.divClass;
            listClass = other.listClass;
            isCaseSensitiveTocTag = other.isCaseSensitiveTocTag;
        }

        protected AsMutable(TocOptions.AsMutable other) {
            levels = other.levels;
            isTextOnly = other.isTextOnly;
            isNumbered = other.isNumbered;
            listType = other.listType;
            isHtml = other.isHtml;
            titleLevel = other.titleLevel;
            title = other.title;
            rawTitleLevel = other.rawTitleLevel;
            rawTitle = other.rawTitle;
            isAstAddOptions = other.isAstAddOptions;
            isBlankLineSpacer = other.isBlankLineSpacer;
            divClass = other.divClass;
            listClass = other.listClass;
            isCaseSensitiveTocTag = other.isCaseSensitiveTocTag;
        }

        @Override
        public TocOptions toImmutable() {
            return new TocOptions(this);
        }

        @NotNull
        @Override
        public MutableDataHolder setIn(@NotNull MutableDataHolder dataHolder) {
            return toImmutable().setIn(dataHolder);
        }

        public AsMutable setLevelList(int... levelList) {
            int levels = 0;
            for (int level : levelList) {
                if (level < 1 || level > 6) throw new IllegalArgumentException("TocOption level out of range [1, 6]");
                levels |= 1 << level;
            }
            this.levels = levels;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TocOptions || o instanceof TocOptions.AsMutable)) return false;

            AsMutable options = o instanceof TocOptions.AsMutable ? (TocOptions.AsMutable) o : ((TocOptions) o).toMutable();

            if (levels != options.levels) return false;
            if (isTextOnly != options.isTextOnly) return false;
            if (isNumbered != options.isNumbered) return false;
            if (listType != options.listType) return false;
            if (isHtml != options.isHtml) return false;
            if (titleLevel != options.titleLevel) return false;
            if (!title.equals(options.title)) return false;
            if (!divClass.equals(options.divClass)) return false;
            if (!listClass.equals(options.listClass)) return false;
            if (rawTitleLevel != options.rawTitleLevel) return false;
            if (!rawTitle.equals(options.rawTitle)) return false;
            if (isAstAddOptions != options.isAstAddOptions) return false;
            if (isBlankLineSpacer != options.isBlankLineSpacer) return false;
            if (isCaseSensitiveTocTag != options.isCaseSensitiveTocTag) return false;
            return true;
        }

        @Override
        public int hashCode() {
            int result = levels;
            result = 31 * result + (isTextOnly ? 1 : 0);
            result = 31 * result + (isNumbered ? 1 : 0);
            result = 31 * result + listType.hashCode();
            result = 31 * result + (isHtml ? 1 : 0);
            result = 31 * result + titleLevel;
            result = 31 * result + title.hashCode();
            result = 31 * result + divClass.hashCode();
            result = 31 * result + listClass.hashCode();
            result = 31 * result + rawTitleLevel;
            result = 31 * result + rawTitle.hashCode();
            result = 31 * result + (isAstAddOptions ? 1 : 0);
            result = 31 * result + (isBlankLineSpacer ? 1 : 0);
            result = 31 * result + (isCaseSensitiveTocTag ? 1 : 0);
            return result;
        }

        @Override
        public String toString() {
            return "TocOptions { " +
                    "levels=" + levels +
                    ", isHtml=" + isHtml +
                    ", isTextOnly=" + isTextOnly +
                    ", isNumbered=" + isNumbered +
                    ", titleLevel=" + titleLevel +
                    ", title='" + title + '\'' +
                    ", rawTitleLevel=" + rawTitleLevel +
                    ", listType=" + listType +
                    ", rawTitle='" + rawTitle + '\'' +
                    ", divClass='" + divClass + '\'' +
                    ", listClass='" + listClass + '\'' +
                    " }";
        }
    }
}
