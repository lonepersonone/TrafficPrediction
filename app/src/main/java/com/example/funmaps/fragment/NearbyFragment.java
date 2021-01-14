package com.example.funmaps.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.funmaps.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NearbyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearbyFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RadioGroup groupHeader;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Fragment commandFragment;
    Fragment foodFragment;
    Fragment sightFragment;
    Fragment hotelFragment;

    public NearbyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SquareFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NearbyFragment newInstance(String param1, String param2) {
        NearbyFragment fragment = new NearbyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nearby, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        groupHeader = (RadioGroup) view.findViewById(R.id.middlebtn);
        fragmentManager = getChildFragmentManager();

        groupHeader.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //中部页签
                fragmentTransaction = fragmentManager.beginTransaction();

                if (commandFragment != null) {
                    fragmentTransaction.hide(commandFragment);
                }
                if (foodFragment != null) {
                    fragmentTransaction.hide(foodFragment);
                }
                if (sightFragment != null) {
                    fragmentTransaction.hide(sightFragment);
                }
                if (hotelFragment != null) {
                    fragmentTransaction.hide(hotelFragment);
                }
                if (checkedId == R.id.btncommand) {
                    if (commandFragment == null) {
                        commandFragment = new CommandFragment();
                        fragmentTransaction.add(R.id.middleFrame, commandFragment, "command");
                    }
                    fragmentTransaction.show(commandFragment);
                    System.out.println("click command");
                } else if (checkedId == R.id.btnfood) {
                    if (foodFragment == null) {
                        foodFragment = new FoodFragment();
                        fragmentTransaction.add(R.id.middleFrame, foodFragment, "food");
                    }
                    fragmentTransaction.show(foodFragment);
                    System.out.println("click food");
                } else if (checkedId == R.id.btnsight) {
                    if (sightFragment == null) {
                        sightFragment = new SightFragment();
                        fragmentTransaction.add(R.id.middleFrame, sightFragment, "sight");
                    }
                    fragmentTransaction.show(sightFragment);
                    System.out.println("click sight");
                } else if (checkedId == R.id.btnhotel) {
                    if (hotelFragment == null) {
                        hotelFragment = new HotelFragment();
                        fragmentTransaction.add(R.id.middleFrame, hotelFragment, "hotel");
                    }
                    fragmentTransaction.show(hotelFragment);
                    System.out.println("click hotel");
                }
            }

        });
    }
}