package cc.tachi.passwordrecorder.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cc.tachi.passwordrecorder.R;

/**
 * Created by m on 2016/10/5.
 */
public class FragmentAbout extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  =  inflater.inflate(R.layout.about,container,false);
        getActivity().setTitle("关于");
        return view;
    }
}
