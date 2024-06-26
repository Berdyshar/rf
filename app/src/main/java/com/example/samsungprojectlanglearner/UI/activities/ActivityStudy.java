package com.example.samsungprojectlanglearner.UI.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import com.example.samsungprojectlanglearner.R;
import com.example.samsungprojectlanglearner.UI.ResultItem;
import com.example.samsungprojectlanglearner.UI.viewModels.DictViewModel;
import com.example.samsungprojectlanglearner.UI.viewModels.ResultViewModel;
import com.example.samsungprojectlanglearner.data.Comp.Comp;
import com.example.samsungprojectlanglearner.data.Comp.CompList;
import com.example.samsungprojectlanglearner.databinding.ActivityStudyBinding;

import java.util.LinkedList;
import java.util.Random;


public class ActivityStudy extends AppCompatActivity {
    private Context context;
    private static AlertDialog dialog;
    private ActivityStudyBinding binding;
    public static LinkedList<ResultItem> resultItemLinkedList;
    private Random random;

    private int right = 0;
    private int wrong = 0;
    private ResultViewModel resultViewModel;
    public static String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        resultViewModel = new ViewModelProvider(this).get(ResultViewModel.class);
        context = this;
        random = new Random();
        LinkedList<Comp> compList = (LinkedList<Comp>) CompList.toArray(DictViewModel.dict.getComps());
        resultItemLinkedList = new LinkedList<>();
        if (compList.isEmpty()) {
            Toast.makeText(this, "Sorry, but you can't study empty dict", Toast.LENGTH_LONG).show();
            finish();
        }
        test(compList);
    }

    private void test(LinkedList<Comp> compList) {
        int number = random.nextInt(compList.size());
        Comp comp = compList.get(number);
        binding.tvQuestion.setText(comp.getWord());
        binding.btnAnswer.setOnClickListener(v -> {
            String str = String.valueOf(binding.etInputTranslationStudy.getText());
            if (str.isEmpty()) {
                str = "No answer given";
            }
            if (str.equalsIgnoreCase(comp.getTranslation())) {
                right++;
                showDialog("Right");
            }
            else {
                ResultItem resultItem = new ResultItem(
                        comp.getWord(),
                        comp.getTranslation(),
                        str
                );
                resultItemLinkedList.add(resultItem);
                wrong++;
                showDialog("wrong");
            }
            compList.remove(number);
            binding.etInputTranslationStudy.setText("");
            if (compList.isEmpty()) {
                new Handler().postDelayed(() -> {
                    result = String.valueOf(right * 100/ (right + wrong));
                    ResultViewModel.setResult();
                    ResultViewModel.setResultItemLinkedList();

                //    ResultViewModel.setResult(String.valueOf(right * 100/ (right + wrong)));
                 //   ResultViewModel.setResultItemLinkedList(resultItemLinkedList);
                    startActivity(new Intent(ActivityStudy.this, ResultActivity.class));
                    finish();

                }, 1500);
                return;
            }
            test(compList);
        });
    }

    private void showDialog(String which) {
        if (which.equals("Right")) {
            dialog = new AlertDialog.Builder(context)
                    .setView(R.layout.window_dialog_right)
                    .create();
        }
        else {
            dialog = new AlertDialog.Builder(context)
                    .setView(R.layout.window_dialog_wrong)
                    .create();
        }
        dialog.show();
        new Handler().postDelayed(() -> dialog.dismiss(), 1500);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}