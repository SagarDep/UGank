package me.bakumon.ugank.module.category;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import es.dmoral.toasty.Toasty;
import me.bakumon.ugank.GlobalConfig;
import me.bakumon.ugank.R;
import me.bakumon.ugank.base.BaseFragment;
import me.bakumon.ugank.databinding.FragmentBinding;
import me.bakumon.ugank.entity.CategoryResult;
import me.bakumon.ugank.entity.Favorite;
import me.bakumon.ugank.module.home.HomeActivity;
import me.bakumon.ugank.module.webview.WebViewActivity;
import me.bakumon.ugank.widget.RecycleViewDivider;

/**
 * CategoryFragment
 *
 * @author bakumon
 * @date 2016/12/8
 */
public class CategoryFragment extends BaseFragment implements CategoryContract.View, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener, BaseQuickAdapter.OnItemChildClickListener {

    private static final String CATEGORY_NAME = "me.bakumon.ugank.module.category.CATEGORY_NAME";

    private FragmentBinding mBinding;

    private CategoryListAdapter mCategoryListAdapter;
    private CategoryContract.Presenter mPresenter = new CategoryPresenter(this);

    private String mCategoryName;

    public static CategoryFragment newInstance(String mCategoryName) {
        CategoryFragment categoryFragment = new CategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CATEGORY_NAME, mCategoryName);
        categoryFragment.setArguments(bundle);
        return categoryFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCategoryName = bundle.getString(CATEGORY_NAME);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        mBinding = getDataBinding();
        mBinding.swipeRefreshLayout.setColorSchemeResources(
                R.color.colorSwipeRefresh1,
                R.color.colorSwipeRefresh2,
                R.color.colorSwipeRefresh3,
                R.color.colorSwipeRefresh4,
                R.color.colorSwipeRefresh5,
                R.color.colorSwipeRefresh6);
        mBinding.swipeRefreshLayout.setOnRefreshListener(this);

        mCategoryListAdapter = new CategoryListAdapter(null);

        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.recyclerView.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.HORIZONTAL));
        mBinding.recyclerView.setAdapter(mCategoryListAdapter);
        mCategoryListAdapter.setOnItemChildClickListener(this);
        mCategoryListAdapter.setOnLoadMoreListener(this, mBinding.recyclerView);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == HomeActivity.SETTING_REQUEST_CODE) {
            mCategoryListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.subscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    @Override
    public String getCategoryName() {
        return this.mCategoryName;
    }

    @Override
    public void showSwipeLoading() {
        mBinding.swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideSwipeLoading() {
        mBinding.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        mPresenter.getCategoryItems(true);
    }

    @Override
    public void setLoading() {

    }

    @Override
    public void getCategoryItemsFail(String failMessage) {
        if (getUserVisibleHint()) {
            Toasty.error(getActivity(), failMessage).show();
        }
    }

    @Override
    public void setCategoryItems(CategoryResult categoryResult) {
        mCategoryListAdapter.setNewData(categoryResult.results);
    }

    @Override
    public void addCategoryItems(CategoryResult categoryResult) {
        mCategoryListAdapter.addData(categoryResult.results);
        mCategoryListAdapter.loadMoreComplete();
    }

    @Override
    public void onLoadMoreRequested() {
        mPresenter.getCategoryItems(false);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        switch (view.getId()) {
            case R.id.ll_item:
                List<CategoryResult.ResultsBean> beans = mCategoryListAdapter.getData();
                if (mCategoryListAdapter.getData().get(position) == null) {
                    Toasty.error(getActivity(), "数据异常").show();
                    return;
                }
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra(WebViewActivity.GANK_TITLE, beans.get(position).desc);
                intent.putExtra(WebViewActivity.GANK_URL, beans.get(position).url);
                Favorite favorite = new Favorite();
                favorite.setAuthor(beans.get(position).who);
                favorite.setData(beans.get(position).publishedAt);
                favorite.setTitle(beans.get(position).desc);
                favorite.setType(beans.get(position).type);
                favorite.setUrl(beans.get(position).url);
                favorite.setGankID(beans.get(position)._id);
                intent.putExtra(WebViewActivity.FAVORITE_DATA, favorite);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
