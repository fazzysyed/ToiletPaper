package net.myerichsen.toiletpaper.ui.products;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import net.myerichsen.toiletpaper.R;
import net.myerichsen.toiletpaper.database.ProductDbAdapter;

import java.util.List;

// TODO Double tapping opens the details fragment
public class ProductFragment extends Fragment {
    final TableRow.LayoutParams llp = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    ProductDbAdapter helper;
    private View root;
    private Context context;

    public static ProductFragment newInstance() {
        return new ProductFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.product_fragment, container, false);
        context = getContext();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        helper = new ProductDbAdapter(context);

        TableLayout tableLayout = root.findViewById(R.id.productTableLayout);

        TableRow tableRow = new TableRow(context);
        tableRow.setBackgroundColor(Color.BLACK);
        tableRow.setPadding(2, 2, 2, 2);
        tableRow.addView(addCell("UID"));
        tableRow.addView(addCell("Varenummer"));
        tableRow.addView(addCell("Varemærke"));
        tableLayout.addView(tableRow);

        List<ProductData> lpd = null;
        try {
            lpd = helper.getAllProductData(context);
        } catch (Exception e) {
            Snackbar snackbar = Snackbar
                    .make(getActivity().findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        ProductData pd;

        for (int i = 0; i < lpd.size(); i++) {
            pd = lpd.get(i);

            tableRow = new TableRow(context);
            tableRow.setBackgroundColor(Color.BLACK);
            tableRow.setPadding(2, 2, 2, 2); //Border between rows

            tableRow.addView(addCell(Integer.toString(pd.getUid())));
            tableRow.addView(addCell(pd.getItemNo()));
            tableRow.addView(addCell(pd.getBrand()));
            tableLayout.addView(tableRow);
        }

//        tableListLayout.addView(tableLayout);


    }

    private LinearLayout addCell(String cellData) {
        llp.setMargins(2, 2, 2, 2);

        LinearLayout cell;
        cell = new LinearLayout(context);
        cell.setBackgroundColor(Color.WHITE);
        cell.setLayoutParams(llp);//2px border on the right for the cell

        TextView tv = new TextView(context);
        tv.setText(cellData);
        tv.setPadding(0, 0, 4, 3);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
        cell.addView(tv);
        return cell;
    }

}
