package com.sxonecard.ui;

import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.sxonecard.R;
import com.sxonecard.base.BaseFragment;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by pc on 2017-04-25.
 */

public class FragmentFour extends BaseFragment {
    private static final String TAG = "FragmentFour";


    @Bind(R.id.inputMoney)
    EditText inputMoney;
    @Bind(R.id.inputPrice)
    GridView inputPrice;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_4;
    }

    @Override
    public void initView() {
        Log.i(TAG, "initView: ====");

        inputMoney.setEnabled(false);
        List<String> inputNumberLst = new ArrayList<>(16);
        inputNumberLst.add("1");
        inputNumberLst.add("2");
        inputNumberLst.add("3");
        inputNumberLst.add("取消");
        inputNumberLst.add("4");
        inputNumberLst.add("5");
        inputNumberLst.add("6");
        inputNumberLst.add("更正");
        inputNumberLst.add("7");
        inputNumberLst.add("8");
        inputNumberLst.add("9");
        inputNumberLst.add(" ");
        inputNumberLst.add("0");
        inputNumberLst.add(".");
        inputNumberLst.add("00");
        inputNumberLst.add("确认");

        inputPrice.setAdapter(new CommonAdapter<String>(getContext(), R.layout.item_number_4, inputNumberLst) {
            @Override
            protected void convert(ViewHolder viewHolder, String item, int position) {
                viewHolder.setText(R.id.number, item);
            }
        });

        inputPrice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            StringBuilder numberBuffer = new StringBuilder();

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //1￥.
                        numberBuffer.append(1);
                        break;
                    case 1:
                        //2￥.
                        numberBuffer.append(2);
                        break;
                    case 2:
                        //3￥.
                        numberBuffer.append(3);
                        break;
                    case 3:
                        //取消.
                        numberBuffer.delete(0, 1000);
                        navHandle.sendEmptyMessage(2);
                        break;
                    case 4:
                        //4￥.
                        numberBuffer.append(4);
                        break;
                    case 5:
                        //5￥.
                        numberBuffer.append(5);
                        break;
                    case 6:
                        //6￥.
                        numberBuffer.append(6);
                        break;
                    case 7:
                        //更正.
                        numberBuffer.delete(0, 1000);
                        break;
                    case 8:
                        //7￥.
                        numberBuffer.append(7);
                        break;
                    case 9:
                        //8￥.
                        numberBuffer.append(8);
                        break;
                    case 10:
                        //9￥.
                        numberBuffer.append(9);
                        break;
                    case 11:
                        //空￥.
                        System.out.println("没有功能的空按钮...");
                        break;
                    case 12:
                        //0￥.
                        numberBuffer.append(0);
                        break;
                    case 13:
                        //小数点.￥.
                        numberBuffer.append(".");
                        break;
                    case 14:
                        //两个0￥.
                        numberBuffer.append("00");
                        break;
                    case 15:
                        //确认.
                        Message msgPrice = Message.obtain();
                        if (null == inputMoney.getText() || "".equals(inputMoney.getText())) {
                            Toast.makeText(FragmentFour.super.context, "请输入充值金额...",
                                    Toast.LENGTH_SHORT).show();
                            msgPrice.what = 3;
                        } else {
                            msgPrice.obj = inputMoney.getText().toString();
                            msgPrice.what = 4;
                        }
                        navHandle.sendMessage(msgPrice);
                        break;
                    default:
                        Toast.makeText(FragmentFour.super.context, "输入金额异常...",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
                inputMoney.setText(numberBuffer);
            }
        });
    }

    @Override
    public void loadData() {

    }
}
