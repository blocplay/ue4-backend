import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.steam.client.SteamApiClient;
import com.tokenplay.ue4.steam.client.types.ApiException;
import com.tokenplay.ue4.steam.client.types.api.CheckAppOwnershipRS;

@Slf4j
public class SteamApiTester {
    public static void main(String[] args) throws ApiException {
        log.info("Testing steam API!!");

        try (SteamApiClient steamApiClient = new SteamApiClient()) {
            steamApiClient.init();
            String steamid = "76561198002796608";
            CheckAppOwnershipRS checkAppOwnershipRS = steamApiClient.checkVeteranPackOwnership(steamid);
            log.info("checkAppOwnershipRS: {}", checkAppOwnershipRS);
        }
    }
}
