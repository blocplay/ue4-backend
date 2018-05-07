import com.tokenplay.ue4.utils.BCrypt;

public class PasswordCreator {

    public static void main(String[] args) {
        System.err.println(BCrypt.hashpw("test", BCrypt.gensalt()));

        System.err.println(BCrypt.checkpw("test", "$2a$10$4CipFiYSYPIm0Yl/68.NMO0L1z.hu5TSHFBzGy46FD1Hr8BnjrFDa"));
    }

}
