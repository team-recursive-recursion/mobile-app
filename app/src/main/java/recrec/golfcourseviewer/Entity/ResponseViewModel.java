package recrec.golfcourseviewer.Entity;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

public class ResponseViewModel extends AndroidViewModel{

    public MutableLiveData<String> courseNamesAndIdJSON = new MutableLiveData<>();

    public ResponseViewModel(@NonNull Application application) {
        super(application);
    }
}
