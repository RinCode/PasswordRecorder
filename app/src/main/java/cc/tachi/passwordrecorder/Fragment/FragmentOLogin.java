package cc.tachi.passwordrecorder.Fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cc.tachi.passwordrecorder.R;

/**
 * Created by tachi on 2016/12/21.
 */

public class FragmentOLogin extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_onlinelogin, container, false);
        getActivity().setTitle("在线登录");
        return view;
    }
}
