package com.huanchengfly.tieba.widgets.theme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;

import com.huanchengfly.theme.interfaces.Tintable;
import com.huanchengfly.theme.utils.ThemeUtils;
import com.huanchengfly.tieba.post.R;
import com.huanchengfly.tieba.post.utils.ThemeUtil;

import java.lang.reflect.Field;

public class TintToolbar extends Toolbar implements Tintable {
    public static final String TAG = "TintToolbar";

    private int mBackgroundTintResId;
    private int mItemTintResId;
    private int mSecondaryItemTintResId;
    private int mActiveItemTintResId;

    public TintToolbar(Context context) {
        this(context, null);
    }

    public TintToolbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.toolbarStyle);
    }

    @SuppressLint("CustomViewStyleable")
    public TintToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            return;
        }
        if (attrs == null) {
            mBackgroundTintResId = R.color.default_color_toolbar;
            mItemTintResId = R.color.default_color_toolbar_item;
            mSecondaryItemTintResId = R.color.default_color_toolbar_item_secondary;
            mActiveItemTintResId = R.color.default_color_toolbar_item_active;
            applyTintColor();
            return;
        }
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.TintToolbar, defStyleAttr, 0);
        mBackgroundTintResId = array.getResourceId(R.styleable.TintToolbar_toolbarBackgroundTint, R.color.default_color_toolbar);
        mItemTintResId = array.getResourceId(R.styleable.TintToolbar_itemTint, R.color.default_color_toolbar_item);
        mSecondaryItemTintResId = array.getResourceId(R.styleable.TintToolbar_secondaryItemTint, R.color.default_color_toolbar_item_secondary);
        mActiveItemTintResId = array.getResourceId(R.styleable.TintToolbar_activeItemTint, R.color.default_color_toolbar_item_active);
        array.recycle();
        applyTintColor();
    }

    @Override
    public void tint() {
        applyTintColor();
    }

    private void applyTintColor() {
        setTitleTextAppearance(getContext(), R.style.TextAppearance_Title);
        setSubtitleTextAppearance(getContext(), R.style.TextAppearance_Subtitle);
        fixColor();
        tintBackground();
        tintNavigationIcon();
        tintOverflowIcon();
        //tintOverflowMenu();
        tintMenuIcon();
        setTitleTextColor(ThemeUtils.getColorById(getContext(), mItemTintResId));
        setSubtitleTextColor(ThemeUtils.getColorById(getContext(), mSecondaryItemTintResId));
    }

    private void fixColor() {
        if (mBackgroundTintResId == 0) {
            mBackgroundTintResId = R.color.default_color_toolbar;
        }
        if (mItemTintResId == 0) {
            mItemTintResId = R.color.default_color_toolbar_item;
        }
        if (mSecondaryItemTintResId == 0) {
            mSecondaryItemTintResId = R.color.default_color_toolbar_item_secondary;
        }
        if (mActiveItemTintResId == 0) {
            mActiveItemTintResId = R.color.default_color_toolbar_item_active;
        }
    }

    private void tintBackground() {
        if (getBackground() == null) {
            setBackgroundColor(ThemeUtils.getColorById(getContext(), mBackgroundTintResId));
        } else {
            setBackgroundTintList(ColorStateList.valueOf(ThemeUtils.getColorById(getContext(), mBackgroundTintResId)));
        }
    }

    @Override
    public void setNavigationIcon(int resId) {
        super.setNavigationIcon(resId);
        applyTintColor();
    }

    private void tintMenuIcon() {
        for (int i = 0; i < getMenu().size(); i++) {
            MenuItem menuItem = getMenu().getItem(i);
            Drawable drawable = menuItem.getIcon();
            if (drawable == null) {
                continue;
            }
            int[][] states = new int[3][];
            states[0] = new int[]{android.R.attr.state_checked};
            states[1] = new int[]{android.R.attr.state_enabled};
            states[2] = new int[]{};
            ColorStateList colorStateList = new ColorStateList(states, new int[]{ThemeUtils.getColorById(getContext(), mActiveItemTintResId),
                    ThemeUtils.getColorById(getContext(), mItemTintResId),
                    ThemeUtils.getColorById(getContext(), mSecondaryItemTintResId),
            });
            drawable.setTintList(colorStateList);
            drawable.invalidateSelf();
            menuItem.setIcon(drawable);
            Log.i(TAG, "tintMenuIcon: " + i + "finish");
        }
    }

    private void tintNavigationIcon() {
        Drawable drawable = getNavigationIcon();
        if (drawable == null) {
            return;
        }
        setNavigationIcon(ThemeUtils.tintDrawable(drawable, ThemeUtils.getColorById(getContext(), mItemTintResId)));
    }

    private void tintOverflowIcon() {
        Drawable drawable = getOverflowIcon();
        if (drawable == null) {
            return;
        }
        setOverflowIcon(ThemeUtils.tintDrawable(drawable, ThemeUtils.getColorById(getContext(), mItemTintResId)));
    }

    @Override
    public void inflateMenu(int resId) {
        super.inflateMenu(resId);
        applyTintColor();
    }

    @SuppressLint("RestrictedApi")
    public void tintOverflowMenu() {
        int popupTheme = R.style.OverflowMenu;
        switch (ThemeUtil.getTheme(getContext())) {
            case ThemeUtil.THEME_BLUE_DARK:
                popupTheme = R.style.OverflowMenu_Dark;
                break;
            case ThemeUtil.THEME_AMOLED_DARK:
                popupTheme = R.style.OverflowMenu_Dark_Amoled;
                break;
            default:
                break;
        }
        setPopupTheme(popupTheme);
        try {
            getMenu();
            Field field = Toolbar.class.getDeclaredField("mMenuView");
            field.setAccessible(true);
            ActionMenuView actionMenuView = (ActionMenuView) field.get(this);
            if (actionMenuView == null) {
                return;
            }
            actionMenuView.setPopupTheme(popupTheme);
            Log.i(TAG, "tintOverflowMenu: finish");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void setBackgroundTintResId(int mBackgroundTintResId) {
        this.mBackgroundTintResId = mBackgroundTintResId;
        tint();
    }

    public void setItemTintResId(int mItemTintResId) {
        this.mItemTintResId = mItemTintResId;
        tint();
    }

    public void setSecondaryItemTintResId(int mSecondaryItemTintResId) {
        this.mSecondaryItemTintResId = mSecondaryItemTintResId;
        tint();
    }

    public void setActiveItemTintResId(int mActiveItemTintResId) {
        this.mActiveItemTintResId = mActiveItemTintResId;
        tint();
    }
}
