/*
 * Copyright (c) 2020. Michael Erichsen.
 *
 * The program is distributed under the terms of the GNU Affero General Public License v3.0
 */

package net.myerichsen.toiletpaper.ui.prices;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import net.myerichsen.toiletpaper.R;
import net.myerichsen.toiletpaper.TPDbAdapter;
import net.myerichsen.toiletpaper.ui.products.ProductModel;

import java.util.List;
import java.util.Objects;

import static net.myerichsen.toiletpaper.ui.home.HomeFragment.BRAND;
import static net.myerichsen.toiletpaper.ui.home.HomeFragment.ITEM_NO;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PriceSelectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@SuppressWarnings("unused")
public class PriceSelectFragment extends Fragment {
    private TPDbAdapter adapter;
    private Activity activity;
    private View snackView;

    private TextInputEditText pItemNoEditText;
    private TextInputEditText pBrandEditText;

    private String brand;
    private String itemNo;

    public PriceSelectFragment() {
        // Required empty public constructor
    }

    private static void hideSoftKeyboard(Activity activity) {
        if (activity.getCurrentFocus() == null) {
            return;
        }

        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    @SuppressWarnings("unused")
    public static PriceSelectFragment newInstance() {
        PriceSelectFragment fragment = new PriceSelectFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.setFragmentResultListener("itemNoRequestKey", this,
                itemNoFragmentResultListener());
        fragmentManager.setFragmentResultListener("brandRequestKey", this,
                brandFragmentResultListener());
    }

    private void complain() {
        Activity activity = getActivity();

        if (Objects.requireNonNull(activity).getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
        }
        Snackbar.make(snackView, "Indtast varenummer eller varemærke", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_price_select, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = getActivity();
        Context context = getContext();
        adapter = new TPDbAdapter(context);
        snackView = requireActivity().findViewById(android.R.id.content);

        pItemNoEditText = view.findViewById(R.id.pItemNoEditText);
        pBrandEditText = view.findViewById(R.id.pBrandEditText);

        AppCompatImageButton searchItemNoBtn = view.findViewById(R.id.pSearchItemNoBtn);
        searchItemNoBtn.setOnClickListener(searchItemNoBtnOnClickListener());

        AppCompatImageButton searchBrandBtn = view.findViewById(R.id.pSearchBrandBtn);
        searchBrandBtn.setOnClickListener(searchBrandBtnOnclickListener());

        AppCompatImageButton priceSelectionBtn = view.findViewById(R.id.priceSelectionBtn);
        priceSelectionBtn.setOnClickListener(priceSelectionBtnOnClickListener());

        AppCompatImageButton graphSelectionBtn = view.findViewById(R.id.graphSelectionBtn);
        graphSelectionBtn.setOnClickListener(graphSelectionBtnOnClickListener());
    }

    private View.OnClickListener graphSelectionBtnOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String itemNo = Objects.requireNonNull(pItemNoEditText.getText()).toString();
                    String brand = Objects.requireNonNull(pBrandEditText.getText()).toString();

                    if (itemNo.equals("") && brand.equals("")) {
                        complain();
                    } else {
                        PriceSelectFragmentDirections.ActionNavPriceSelectToNavPriceGraph action =
                                PriceSelectFragmentDirections.actionNavPriceSelectToNavPriceGraph((Objects.requireNonNull(pItemNoEditText.getText()).toString()),
                                        Objects.requireNonNull(pBrandEditText.getText()).toString());
                        Navigation.findNavController(v).navigate(action);
                    }
                } catch (Exception e) {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content), Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_LONG).show();
                }
            }
        };
    }

    private View.OnClickListener priceSelectionBtnOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String itemNo = Objects.requireNonNull(pItemNoEditText.getText()).toString();
                    String brand = Objects.requireNonNull(pBrandEditText.getText()).toString();

                    if (itemNo.equals("") && brand.equals("")) {
                        complain();
                    } else {
                        PriceSelectFragmentDirections.ActionNavPriceSelectToNavPricesList action =
                                PriceSelectFragmentDirections.actionNavPriceSelectToNavPricesList(Objects.requireNonNull(pItemNoEditText.getText()).toString(),
                                        Objects.requireNonNull(pBrandEditText.getText()).toString());
                        Navigation.findNavController(v).navigate(action);
                    }
                } catch (Exception e) {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content), Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_LONG).show();
                }
            }
        };
    }

    private View.OnClickListener searchItemNoBtnOnClickListener() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                hideSoftKeyboard(activity);
                itemNo = Objects.requireNonNull(pItemNoEditText.getText()).toString();
                if (itemNo.equals("")) {
                    Snackbar.make(snackView,
                            R.string.enter_itemno_prompt, Snackbar.LENGTH_LONG).show();
                } else {
                    PriceSelectFragmentDirections.ActionNavPriceSelectToNavItemNo action =
                            PriceSelectFragmentDirections.actionNavPriceSelectToNavItemNo(itemNo);
                    Navigation.findNavController(v).navigate(action);
                }
            }
        };
    }

    private View.OnClickListener searchBrandBtnOnclickListener() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                hideSoftKeyboard(activity);
                brand = Objects.requireNonNull(pBrandEditText.getText()).toString();
                if (brand.equals("")) {
                    Snackbar.make(snackView,
                            R.string.enter_brand_prompt, Snackbar.LENGTH_LONG).show();
                } else {
                    PriceSelectFragmentDirections.ActionNavPriceSelectToNavBrand action =
                            PriceSelectFragmentDirections.actionNavPriceSelectToNavBrand(brand);
                    Navigation.findNavController(v).navigate(action);
                }
            }
        };
    }

    private FragmentResultListener itemNoFragmentResultListener() {
        return new FragmentResultListener() {

            /**
             * Callback used to handle results passed between fragments.
             *
             * @param itemNoRequestKey key used to store the result
             * @param bundle result passed to the callback
             */
            @Override
            public void onFragmentResult(@NonNull String itemNoRequestKey, @NonNull Bundle bundle) {
                String result = bundle.getString(ITEM_NO);

                try {
                    List<ProductModel> lpm = adapter.getProductModels("ITEM_NO=?", result);

                    if (lpm.size() == 0) {
                        Snackbar.make(snackView,
                                R.string.itemno_not_found, Snackbar.LENGTH_LONG).show();
                    } else {
                        pItemNoEditText.setText(lpm.get(0).getItemNo());
                        pBrandEditText.setText(lpm.get(0).getBrand());
                    }
                } catch (Exception e) {
                    Snackbar.make(snackView, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_LONG).show();
                }
            }
        };
    }

    private FragmentResultListener brandFragmentResultListener() {
        return new FragmentResultListener() {

            /**
             * Callback used to handle results passed between fragments.
             *
             * @param brandRequestKey key used to store the result
             * @param bundle result passed to the callback
             */
            @Override
            public void onFragmentResult(@NonNull String brandRequestKey, @NonNull Bundle bundle) {
                String result = bundle.getString(BRAND);

                try {
                    List<ProductModel> lpm = adapter.getProductModels("BRAND=?", result);

                    if (lpm.size() == 0) {
                        Snackbar.make(snackView,
                                R.string.brand_not_found, Snackbar.LENGTH_LONG).show();
                    } else {
                        pItemNoEditText.setText(lpm.get(0).getItemNo());
                        pBrandEditText.setText(lpm.get(0).getBrand());
                    }
                } catch (Exception e) {
                    Snackbar.make(snackView, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_LONG).show();
                }
            }
        };
    }
}