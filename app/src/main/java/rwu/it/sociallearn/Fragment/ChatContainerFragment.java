package rwu.it.sociallearn.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import rwu.it.sociallearn.Chat_Module.ChatsPagerAdapter;
import rwu.it.sociallearn.R;

public class ChatContainerFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_container, container, false);

        tabLayout = view.findViewById(R.id.tabLayoutChats);
        viewPager = view.findViewById(R.id.viewPagerChats);

        ChatsPagerAdapter adapter = new ChatsPagerAdapter(getActivity());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 1:
                            tab.setText("Groups");
                            break;
                        case 2:
                            tab.setText("Personal");
                            break;
                        default:
                            tab.setText("All");
                    }
                }).attach();

        return view;
    }
}
