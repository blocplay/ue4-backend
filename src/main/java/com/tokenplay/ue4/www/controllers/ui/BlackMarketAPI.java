package com.tokenplay.ue4.www.controllers.ui;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.tools.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.model.db.tables.records.BmAccountsRecord;
import com.tokenplay.ue4.model.db.tables.records.BmAssetsRecord;
import com.tokenplay.ue4.model.db.tables.records.BmTransactionsRecord;
import com.tokenplay.ue4.model.db.tables.records.LoreCorporationsRecord;
import com.tokenplay.ue4.model.db.tables.records.LoreEquipmentRecord;
import com.tokenplay.ue4.model.db.tables.records.PilotRecord;
import com.tokenplay.ue4.www.api.CurrencyResponse;
import com.tokenplay.ue4.www.api.JSONResponse;
import com.tokenplay.ue4.www.controllers.CommonAPI;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Controller
@Transactional
@RequestMapping(value = "/bm")
public class BlackMarketAPI extends CommonAPI {
    @Autowired
    public BlackMarketAPI(DSLContext jooq) {
        setJooq(jooq);
    }

    // **********************
    @ResponseBody
    @RequestMapping(value = "{token}/rest/totaltec", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 500))
    public JSONResponse totalTec(@PathVariable(value = "token") String token) {
        JSONResponse response = new JSONResponse();
        double currency = 0;
        try {
            BmAccountsRecord account = getAccountsDB().findByPilToken(token);
            if (account != null) {

                currency = account.getBmAccountbalance();
                response = new CurrencyResponse(currency);
                response.setSuccess(true);
            }

        } catch (Exception e) {
            log.error("Error obtaining current balance", e);
            response.setError("Error calculating currency.");
        }

        return response;
    }

    //Return inventory instance record
    //Return the assets table essentially for a particular pilot
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "{token}/rest/pilot/assets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public JSONObject assets(@PathVariable(value = "token") String token) {
        JSONObject response = new JSONObject();
        JSONObject jAssetCollection = new JSONObject();

        log.debug("All assets requested");
        PilotRecord pilot = getPilots().findByToken(token);
        if (pilot != null) {
            //System.out.println(pilot.toString());
            Result<Record> records = getAssetsDB().findByPilId(pilot.getPilId());

            for (Record record : records) {
                BmAssetsRecord a = record.into(BmAssetsRecord.class);
                LoreEquipmentRecord loreEquipment = record.into(LoreEquipmentRecord.class);
                JSONObject jAssetRecord = new JSONObject();
                String getBmAssetId = removeNull(a.getBmAssetId());
                String getBmDefaultId = removeNull(a.getBmDefaultId());
                String getBmParentId = removeNull(a.getBmParentId());
                String getBmAssetCode = removeNull(loreEquipment.getLeqCode());
                String getBmAssetType = removeNull(loreEquipment.getLeqType());
                String getBmAssetPjson = removeNull(a.getBmAssetPjson());

                jAssetRecord.put("IID", getBmAssetId.toString());
                jAssetRecord.put("PILID", pilot.getPilId());
                jAssetRecord.put("DID", getBmDefaultId.toString());
                jAssetRecord.put("IPID", getBmParentId.toString());
                jAssetRecord.put("BIC", getBmAssetCode.toString());
                jAssetRecord.put("TID", getBmAssetType.toString());
                jAssetRecord.put("PRDTJ", getBmAssetPjson.toString());

                jAssetCollection.put(getBmAssetId, jAssetRecord);
            }
        }

        response.put("Collections", jAssetCollection);

        return response;
    }

    //Return inventory instance record - Minimal
    //Return the assets table essentially for a particular pilot
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "{token}/rest/pilot/assetsmin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public JSONObject assetsmin(@PathVariable(value = "token") String token) {
        JSONObject response = new JSONObject();
        JSONObject jAssetCollection = new JSONObject();

        log.error("All assets requested");
        PilotRecord pilot = getPilots().findByToken(token);
        if (pilot != null) {
            //System.out.println(pilot.toString());
            Result<Record> aRecord = getAssetsDB().findByPilId(pilot.getPilId());

            for (Record record : aRecord) {
                BmAssetsRecord a = record.into(BmAssetsRecord.class);
                JSONObject jAssetRecord = new JSONObject();
                String getBmAssetId = removeNull(a.getBmAssetId());
                String getBmDefaultId = removeNull(a.getBmDefaultId());
                String getBmParentId = removeNull(a.getBmParentId());
                jAssetRecord.put("IID", getBmAssetId.toString());
                jAssetRecord.put("DID", getBmDefaultId.toString());
                jAssetRecord.put("IPID", getBmParentId.toString());

                jAssetCollection.put(getBmAssetId, jAssetRecord);
            }
        }

        response.put("Collections", jAssetCollection);

        return response;
    }

    //Return inventory instance record - Slim
    //Return the assets table essentially for a particular pilot
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "{token}/rest/pilot/assetsslim", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public JSONObject assetsslim(@PathVariable(value = "token") String token) {
        JSONObject response = new JSONObject();
        JSONObject jAssetCollection = new JSONObject();

        log.error("All assets requested");
        PilotRecord pilot = getPilots().findByToken(token);
        if (pilot != null) {
            //System.out.println(pilot.toString());
            Result<Record> aRecord = getAssetsDB().findByPilId(pilot.getPilId());

            for (Record record : aRecord) {
                BmAssetsRecord a = record.into(BmAssetsRecord.class);
                JSONObject jAssetRecord = new JSONObject();
                String getBmAssetId = removeNull(a.getBmAssetId());
                String getBmDefaultId = removeNull(a.getBmDefaultId());

                jAssetRecord.put("DID", getBmDefaultId.toString());

                jAssetCollection.put(getBmAssetId, jAssetRecord);
            }
        }

        response.put("Collections", jAssetCollection);

        return response;
    }


    //Sorting function. Return based on Type
    //Return the assets table essentially for a particular pilot
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "{token}/rest/pilot/assetsbytype/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public JSONObject assetsbytype(@PathVariable(value = "token") String token, @PathVariable(value = "type") String type) {

        JSONObject response = new JSONObject();
        JSONObject jAssetCollection = new JSONObject();

        log.debug("All assets by type requested");
        PilotRecord pilot = getPilots().findByToken(token);
        if (pilot != null) {
            //System.out.println(pilot.toString());
            Result<Record> aRecord = getAssetsDB().findByPilIdAndType(pilot.getPilId(), type);

            for (Record record : aRecord) {
                BmAssetsRecord a = record.into(BmAssetsRecord.class);
                LoreEquipmentRecord loreEquipment = record.into(LoreEquipmentRecord.class);
                JSONObject jAssetRecord = new JSONObject();
                String getBmAssetId = removeNull(a.getBmAssetId());
                String getBmDefaultId = removeNull(a.getBmDefaultId());
                String getBmParentId = removeNull(a.getBmParentId());
                String getBmAssetCode = removeNull(loreEquipment.getLeqCode());
                String getBmAssetType = removeNull(loreEquipment.getLeqType());
                String getBmAssetPjson = removeNull(a.getBmAssetPjson());

                jAssetRecord.put("IID", getBmAssetId.toString());
                jAssetRecord.put("PILID", pilot.getPilId());
                jAssetRecord.put("DID", getBmDefaultId.toString());
                jAssetRecord.put("IPID", getBmParentId.toString());
                jAssetRecord.put("BIC", getBmAssetCode.toString());
                jAssetRecord.put("TID", getBmAssetType.toString());
                jAssetRecord.put("PRDTJ", getBmAssetPjson.toString());

                jAssetCollection.put(getBmAssetId, jAssetRecord);
            }
        }

        response.put("Collections", jAssetCollection);

        return response;
    }

    //Sorting function. Return based on Code
    //Return the assets table essentially for a particular pilot
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "{token}/rest/pilot/assetsbycode/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public JSONObject assetsbycode(@PathVariable(value = "token") String token, @PathVariable(value = "code") String code) {
        JSONObject response = new JSONObject();
        JSONObject jAssetCollection = new JSONObject();

        log.debug("All assets by code requested");
        PilotRecord pilot = getPilots().findByToken(token);
        if (pilot != null) {
            //System.out.println(pilot.toString());
            Result<Record> records = getAssetsDB().findByPilIdAndCode(pilot.getPilId(), code);
            for (Record record : records) {
                BmAssetsRecord a = record.into(BmAssetsRecord.class);
                LoreEquipmentRecord loreEquipment = record.into(LoreEquipmentRecord.class);
                JSONObject jAssetRecord = new JSONObject();
                String getBmAssetId = removeNull(a.getBmAssetId());
                String getBmDefaultId = removeNull(a.getBmDefaultId());
                String getBmParentId = removeNull(a.getBmParentId());
                String getBmAssetCode = removeNull(loreEquipment.getLeqCode());
                String getBmAssetType = removeNull(loreEquipment.getLeqType());
                String getBmAssetPjson = removeNull(a.getBmAssetPjson());

                jAssetRecord.put("IID", getBmAssetId.toString());
                jAssetRecord.put("PILID", pilot.getPilId());
                jAssetRecord.put("DID", getBmDefaultId.toString());
                jAssetRecord.put("IPID", getBmParentId.toString());
                jAssetRecord.put("BIC", getBmAssetCode.toString());
                jAssetRecord.put("TID", getBmAssetType.toString());
                jAssetRecord.put("PRDTJ", getBmAssetPjson.toString());

                jAssetCollection.put(getBmAssetId, jAssetRecord);
            }
        }

        response.put("Collections", jAssetCollection);

        return response;
    }

    //Sorting function. Return based on Type - Minimal
    //Return the assets table essentially for a particular pilot
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "{token}/rest/pilot/assetsbytypemin/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public JSONObject assetsbytypemin(@PathVariable(value = "token") String token, @PathVariable(value = "type") String type) {
        JSONObject response = new JSONObject();
        JSONObject jAssetCollection = new JSONObject();

        log.debug("All assets minimal by type requested");
        PilotRecord pilot = getPilots().findByToken(token);
        if (pilot != null) {
            //System.out.println(pilot.toString());
            Result<Record> aRecord = getAssetsDB().findByPilIdAndType(pilot.getPilId(), type);

            for (Record record : aRecord) {
                BmAssetsRecord a = record.into(BmAssetsRecord.class);
                JSONObject jAssetRecord = new JSONObject();
                String getBmAssetId = removeNull(a.getBmAssetId());
                String getBmDefaultId = removeNull(a.getBmDefaultId());
                String getBmParentId = removeNull(a.getBmParentId());

                jAssetRecord.put("IID", getBmAssetId.toString());
                jAssetRecord.put("DID", getBmDefaultId.toString());
                jAssetRecord.put("IPID", getBmParentId.toString());

                jAssetCollection.put(getBmAssetId, jAssetRecord);
            }
        }

        response.put("Collections", jAssetCollection);

        return response;
    }

    //Sorting function. Return based on Code - Minimal
    //Return the assets table essentially for a particular pilot
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "{token}/rest/pilot/assetsbycodemin/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public JSONObject assetsbycodemin(@PathVariable(value = "token") String token, @PathVariable(value = "code") String code) {

        JSONObject response = new JSONObject();
        JSONObject jAssetCollection = new JSONObject();

        log.error("All assets minimal by code requested");
        PilotRecord pilot = getPilots().findByToken(token);
        if (pilot != null) {
            //System.out.println(pilot.toString());
            Result<Record> aRecord = getAssetsDB().findByPilIdAndCode(pilot.getPilId(), code);

            for (Record record : aRecord) {
                BmAssetsRecord a = record.into(BmAssetsRecord.class);
                JSONObject jAssetRecord = new JSONObject();
                String getBmAssetId = removeNull(a.getBmAssetId());
                String getBmDefaultId = removeNull(a.getBmDefaultId());
                String getBmParentId = removeNull(a.getBmParentId());

                jAssetRecord.put("IID", getBmAssetId.toString());
                jAssetRecord.put("DID", getBmDefaultId.toString());
                jAssetRecord.put("IPID", getBmParentId.toString());

                jAssetCollection.put(getBmAssetId, jAssetRecord);
            }
        }

        response.put("Collections", jAssetCollection);

        return response;
    }



    //Return market place object
    //Return everything for sale on the marketplace. Complete dump.
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "{token}/rest/marketplace/allequipment", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public JSONObject allequipment(@PathVariable(value = "token") String token) {
        Result<LoreEquipmentRecord> eRecord = null;

        JSONObject response = new JSONObject();
        JSONObject jEquipmentCollection = new JSONObject();

        log.debug("All marketplace assets requested");
        PilotRecord pilot = getPilots().findByToken(token);
        if (pilot != null) {
            //System.out.println(pilot.toString());
            eRecord = getEquipmentDB().findAll();

            for (LoreEquipmentRecord e : eRecord) {
                JSONObject jEquipmentRecord = new JSONObject();
                String getLeqId = removeNull(e.getLeqId());
                String getLeqCode = removeNull(e.getLeqCode());
                String getLeqType = removeNull(e.getLeqType());
                String getLeqManufacture = removeNull(e.getLeqManufacture());
                String getLeqLcoId = removeNull(e.getLeqLcoId());
                String getLeqFaction = removeNull(e.getLeqFaction());
                String getLeqEquipment = removeNull(e.getLeqEquipment());
                String getLeqInoId = removeNull(e.getLeqInoId());
                String getLeqLgeId = removeNull(e.getLeqLgeId());
                String getLeqRounds = removeNull(e.getLeqRounds());
                String getLeqStoreOrderId = removeNull(e.getLeqStoreOrderId());
                String getLeqThreat = removeNull(e.getLeqThreat());
                String getLeqTec = removeNull(e.getLeqTec());


                jEquipmentRecord.put("LEQID", getLeqId.toString());
                jEquipmentRecord.put("LEQCODE", getLeqCode.toString());
                jEquipmentRecord.put("LEQTYPE", getLeqType.toString());
                jEquipmentRecord.put("LEQMAN", getLeqManufacture.toString());
                jEquipmentRecord.put("LEQLCOID", getLeqLcoId.toString());
                jEquipmentRecord.put("LEQFAC", getLeqFaction.toString());
                jEquipmentRecord.put("LEQEQ", getLeqEquipment.toString());
                jEquipmentRecord.put("LEQINOID", getLeqInoId.toString());
                jEquipmentRecord.put("LEQLGEID", getLeqLgeId.toString());
                jEquipmentRecord.put("LEQRO", getLeqRounds.toString());
                jEquipmentRecord.put("LEQSOID", getLeqStoreOrderId.toString());
                jEquipmentRecord.put("LEQTHREAT", getLeqThreat.toString());
                jEquipmentRecord.put("LEQTEC", getLeqTec.toString());

                jEquipmentCollection.put(getLeqId, jEquipmentRecord);
            }
        }

        response.put("Collections", jEquipmentCollection);

        return response;
    }


    //Buy any asset on the marketplace.
    @ResponseBody
    @RequestMapping(value = "{token}/rest/marketplace/buy/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONResponse buyItem(@PathVariable(value = "token") String token, @PathVariable(value = "id") String id) {
        log.debug("Purchasing marketplace item");
        JSONResponse response = new JSONResponse();
        try {
            PilotRecord pilot = getPilots().findByToken(token);
            if (pilot != null) {

                String assetId = newUUID();

                LoreEquipmentRecord equipment = getEquipmentDB().findById(id);
                BmAssetsRecord asset = new BmAssetsRecord();
                asset.setBmAssetId(assetId);
                asset.setBmPilId(pilot.getPilId());
                asset.setBmDefaultId(id);
                asset.attach(getJooq().configuration());
                asset.store();

                String transferId = newUUID();

                BmAccountsRecord pilotAccount = getAccountsDB().findByPilId(pilot.getPilId());

                //Pilot makes a purchase.

                //Remove tec from pilot.
                BmTransactionsRecord transactionP = new BmTransactionsRecord();
                transactionP.setBmTransactionsId(newUUID());
                transactionP.setBmTransferId(transferId);
                transactionP.setBmAccountsId(pilotAccount.getBmAccountsId());
                transactionP.setBmAmount(equipment.getLeqTec().doubleValue() * -1);
                transactionP.setBmPaymentstatus("COMPLETE");
                transactionP.setBmAssetId(assetId);
                transactionP.attach(getJooq().configuration());
                transactionP.store();

                LoreCorporationsRecord corp = getCorporationsDB().findById(equipment.getLeqLcoId());
                BmAccountsRecord corpAccount = getAccountsDB().findByCorpId(corp.getLcoId());


                //Give tec to corp.
                BmTransactionsRecord transactionC = new BmTransactionsRecord();
                transactionC.setBmTransactionsId(newUUID());
                transactionC.setBmTransferId(transferId);
                transactionC.setBmAccountsId(corpAccount.getBmAccountsId());
                transactionC.setBmAmount(equipment.getLeqTec().doubleValue());
                transactionC.setBmPaymentstatus("COMPLETE");
                transactionC.setBmAssetId(assetId);
                transactionC.attach(getJooq().configuration());
                transactionC.store();

                //System.out.println(record);

                response.setSuccess(true);
            }

        } catch (Exception e) {
            response.setError("Error purchasing item.");
            log.error(e.getMessage());
        }

        return response;
    }


    public static String removeNull(Object nullCheck) {
        if (nullCheck == null) {
            return "";
        } else {
            return nullCheck.toString();
        }
    }
}
