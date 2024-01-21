package com.example.bookedup.fragments.accommodations;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;



import com.example.bookedup.R;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Reservation;
import com.example.bookedup.model.SingleStatisticData;
import com.example.bookedup.services.StatisticService;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SingleStatisticFragment extends Fragment {
    private BarChart chart;
    private Bitmap chartBitmap;
    private TextView title;
    private Button downloadPdf;
    private List<Reservation> reservations = new ArrayList<>();
    private Accommodation accommodation;

    public SingleStatisticFragment(Accommodation accommodation, List<Reservation> reservations) {
        this.accommodation = accommodation;
        this.reservations = reservations;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_statistic, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        setChartData();

        downloadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chartBitmap = getChartBitmap();
                if (chartBitmap != null) {
                    try {
                        createAndSavePdf(chartBitmap);
                    } catch (IOException | DocumentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initView(View view) {
        chart = view.findViewById(R.id.statisticChart);
        title = view.findViewById(R.id.title);
        title.setText(accommodation.getName());
        downloadPdf = view.findViewById(R.id.downloadPDF);
    }


    private void setChartData() {

        StatisticService statisticService = new StatisticService();
        List<SingleStatisticData> results = statisticService.getSingleStatistic(accommodation, reservations);

        List<BarEntry> profitEntries = new ArrayList<>();
        List<BarEntry> reservationEntries = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            SingleStatisticData data = results.get(i);

            profitEntries.add(new BarEntry(i, data.getTotalEarnings().floatValue()));
            reservationEntries.add(new BarEntry(i, data.getTotalReservations()));
        }

        BarDataSet profitDataSet = new BarDataSet(profitEntries, "Earnings");
        profitDataSet.setColor(Color.BLUE);

        BarDataSet reservationDataSet = new BarDataSet(reservationEntries, "Number of Reservations");
        reservationDataSet.setColor(Color.RED);

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(profitDataSet);
        dataSets.add(reservationDataSet);

        BarData barData = new BarData(dataSets);
        barData.setBarWidth(0.15f);
        barData.groupBars(0, 0.5f, 0.03f);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getMonthsLabels(results)));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(results.size());
        xAxis.setLabelRotationAngle(45);

        chart.setData(barData);
        chart.animateY(1000);
        chart.invalidate();

    }

    private Bitmap getChartBitmap() {
        chart.setDrawingCacheEnabled(true);
        Bitmap currentChartBitmap = Bitmap.createBitmap(chart.getMeasuredWidth(), chart.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(currentChartBitmap);
        chart.draw(canvas);

        chart.setDrawingCacheEnabled(false);
        chart.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        chart.layout(0, 0, chart.getMeasuredWidth(), chart.getMeasuredHeight());
        return currentChartBitmap;
    }

    private void createAndSavePdf(Bitmap chartBitmap) throws IOException, DocumentException {
        Document document = new Document();
        String pdfFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/single_statistic.pdf";
        File pdfFile = new File(pdfFilePath);

        PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

        document.open();
        document.addTitle(accommodation.getName() + " Statistics");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        chartBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Image chartImage = Image.getInstance(stream.toByteArray());
        chartImage.scaleToFit(PageSize.A4.getWidth() - 50, PageSize.A4.getHeight() - 50);
        document.add(chartImage);

        document.close();

        Toast.makeText(getContext(), "PDF saved in downloads!", Toast.LENGTH_SHORT).show();
    }

    private String[] getMonthsLabels(List<SingleStatisticData> data) {
        String[] monthLabels = new String[data.size()];

        for (int i = 0; i < data.size(); i++) {
            monthLabels[i] = data.get(i).getMonth();
        }

        return monthLabels;
    }

}


