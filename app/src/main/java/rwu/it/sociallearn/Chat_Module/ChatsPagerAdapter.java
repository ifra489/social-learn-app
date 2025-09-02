package rwu.it.sociallearn.Chat_Module;



import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import rwu.it.sociallearn.Fragment.AllChatsFragment;
import rwu.it.sociallearn.Fragment.GroupChatsFragment;
import rwu.it.sociallearn.Fragment.PersonalChatsFragment;

public class ChatsPagerAdapter extends FragmentStateAdapter {

    public ChatsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new GroupChatsFragment();
            case 2:
                return new PersonalChatsFragment();
            default:
                return new AllChatsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
