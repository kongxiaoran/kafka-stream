package finchina.demo.util;

import com.alibaba.fastjson.JSONArray;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author sunwei@finchina.com
 * @Date 2021/11/16 10:01
 * @Description es 脚本工具类 ，包括删除、更新或插入 两种脚本类型
 **/
public class EsScriptUtil {

    private EsScriptUtil() {
    }


    /**
     * @param childItemKey 索引的id，一般情况下面为‘kid’
     * @param map          参数，一般情况下，如果根据kid根据删除的话，那么map中应该有个key为kid
     * @param field        list字段的名称
     * @date 2021/11/16 10:03
     * @description 获取删除某个list中的item脚本
     */
    public static Script getListDeleteItem(String childItemKey, Map<String, String> map, String field) {
        Map<String, Object> params = new HashMap<>();
        params.put(field, map);
        String scriptStr = "if(ctx !=null && ctx._source.containsKey('" + field + "')) {" +
                "for (int i=0;i<ctx._source." + field + ".size();i++)" +
                "{ if(ctx._source." + field + "[i]['" + childItemKey + "'] == params." + field + "." + childItemKey + ")" +
                "{ctx._source." + field + ".remove(i)}}}";

        return new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, scriptStr, params);
    }

    public static Script getListInsert(JSONArray jsonArray, String field) {
        Map<String, Object> params = new HashMap<>();
        params.put(field, jsonArray);
        String scriptStr = "ctx._source." + field + "= params." + field + "";

        return new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, scriptStr, params);
    }

    /**
     * 获取更新或插入脚本<br>
     * 1、表示如果该doc不包含该字段，在该doc新建字段并赋值value，
     * 2、如果存在该字段,会比较传入的对象是否存在于list中存在的对象相等，如果不相等就添加，相等就更新
     *
     * @param childItemKey 索引的id，一般情况下面为‘kid’
     * @param map          参数,里面包括加入list item的必要的字段，需要与已有的list的数据结构保持一致
     * @param field        list字段的名称
     * @date 2021/11/16 10:05
     */
    public static Script getUpdateOrInsert(String childItemKey, Map<String, Object> map, String field) {
        Map<String, Object> params = new HashMap<>();
        params.put(field, map);

        String scriptStr = "if(!ctx._source.containsKey('" + field + "'))" + "{ctx._source." + field + "=[params." + field + "]} " +
                "else {" +
                "  int size=ctx._source.list.size();" +
                "  if(size >= 9999)" +
                "  {size=9999;}" +
                " int has=0; for(int i=0; i< size; i++)" +
                "{ if (ctx._source." + field + "[i]['" + childItemKey + "'] == params." + field + "." + childItemKey + "){" +
                "has = 1; ctx._source." + field + "[i]=params." + field + "; break;}}" +
                "if(has==0 && ctx._source.list.size() < 9999) {ctx._source." + field + ".add(params." + field + ");}}";

        return new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, scriptStr, params);
    }

    /**
     * 获取更新或插入脚本<br>
     * 1、表示如果该doc不包含该字段，在该doc新建字段并赋值value，
     * 2、如果存在该字段,会比较传入的对象是否存在于list中存在的对象相等，如果不相等就添加，相等就更新
     *
     * @param childItemKey 索引的id，一般情况下面为‘kid’
     * @param map          参数,里面包括加入list item的必要的字段，需要与已有的list的数据结构保持一致
     * @param field        list字段的名称
     * @date 2021/11/16 10:05
     */
    public static Script getUpdateOrInsertWithDeleteFor1306(String childItemKey, Map<String, Object> map, String field) {
        Map<String, Object> params = new HashMap<>();
        params.put(field, map);

        String scriptStr = "if(!ctx._source.containsKey('" + field + "'))" + "{ctx._source." + field + "=[params." + field + "]} " +
                "else { " +
                "  int size=ctx._source.list.size();" +
                "  if(size >= 9999)" +
                "  {size=9999;}" +
                " int hasDiffer=0; for(int i=0; i<size; i++)" +
                "{ if (ctx._source." + field + "[i]['" + childItemKey + "'] != params." + field + "." + childItemKey + "){" +
                "hasDiffer = 1; break;}}" +
                "if(hasDiffer==0) {int hasSame=0;for(int j=0; j<ctx._source." + field + ".size(); j++)" +
                "{if (ctx._source." + field + "[j]['" + childItemKey + "'] == params." + field + "." + childItemKey + ")" +
                "{ hasSame =1;ctx._source." + field + "[j]=params." + field + "; break;}}" +
                "if(hasSame==0 && ctx._source.list.size() < 9999) {ctx._source." + field + ".add(params." + field + ");}}}";
        return new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, scriptStr, params);
    }

    /**
     * 获取更新或插入脚本<br>
     * 1、表示如果该doc不包含该字段，在该doc新建字段并赋值value，
     * 2、如果存在该字段,会比较传入的对象是否存在于list中存在的对象相等，如果不相等就添加，相等就更新
     *
     * @param childItemKey 索引的id，一般情况下面为‘kid’
     * @param map          参数,里面包括加入list item的必要的字段，需要与已有的list的数据结构保持一致
     * @param field        list字段的名称
     * @date 2021/11/16 10:05
     */
    public static Script getUpdateOrInsertWithDeleteFor1301_new(String childItemKey, Map<String, Object> map, String field, String id) {
        Map<String, Object> params = new HashMap<>();
        params.put(field, map);
        params.put("id", id);
        String scriptStr = "if(!ctx._source.containsKey('" + field + "'))" + "{ctx._source." + field + "=[params." + field + "]} " +
                "else { " +
                "  int size=ctx._source.list.size();" +
                "  if(size >= 9999)" +
                "  {size=9999;}" +
                "int has=0; for(int i=0; i<size; i++)" +
                "{ if (ctx._source." + field + "[i]['" + childItemKey + "'] == params." + field + "." + childItemKey + "){" +
                "has = 1; ctx._source." + field + "[i]=params." + field + ";}" +
                "if (ctx._source." + field + "[i]['" + childItemKey + "'] == params.id)" +
                "{ctx._source." + field + "[i]['itcode2']=\"null\";" +
                "ctx._source." + field + "[i]['level1']=\"null\";" +
                "ctx._source." + field + "[i]['level2']=\"null\";" +
                "ctx._source." + field + "[i]['org_risk']=\"null\";" +
                "ctx._source." + field + "[i]['trcode']=\"null\";" +
                "ctx._source." + field + "[i]['itcode2']=\"null\";" +
                "ctx._source." + field + "[i]['lastLevel']=\"null\";}}" +
                "if(has==0 && ctx._source.list.size() < 9999) {ctx._source." + field + ".add(params." + field + ");}}";

        return new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, scriptStr, params);
    }

    /**
     * 比较kid是否一致，如果一致则进行更新，如果不一致，则不更新<br>
     * 1、！ctx._source.containsKey('kid') 两种情况：（1）子表数据先来到 （2）主表数据还没到<br>
     * 对于(1)，那么不更新所有字段，只更新flag=1  and isHidden=0,isHidden=0表示主表数据已经到达<br>
     * 对于(2)，不会执行到script这里，在前面会执行upsert操作<br>
     * 2、ctx._source.kid =='params.kid' 则表示当前的这个数据需要置flag=1，那么将isHidden=0表示主表数据已经到达<br>
     *
     * @date 2021/11/24 11:10
     */
    public static Script getCompareAndFlag(String kid) {
        Map<String, Object> params = new HashMap<>();
        params.put("kid", kid);

        String scriptStr = "if(!ctx._source.containsKey('kid') || ctx._source.kid =='params.kid') {ctx._source.flag=1; ctx._source.isHidden=0;} else{ ctx.op='none';}";
        return new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, scriptStr, params);
    }


    public static Script getErrorScript(Map<String, Object> params) {
        String scriptStr = "if(!ctx._source.containsKey('list')) { " +
                " ctx._source.list=[params.list];}" +
                " else { int size=ctx._source.list.size();" +
                " if(size >= 9999)" +
                " {size=9999;}" +
                " int has=0;" +
                "  for(int i=0; i<size; i++) " +
                " {if (ctx._source.list[i]['kid'] == params.list.kid) " +
                " { has=1;" +
                " ctx._source.list[i]=params.list; " +
                " break;}}" +
                " if(has==0 && ctx._source.list.size() < 9999)" +
                " {ctx._source.list.add(params.list);}}";
        return new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, scriptStr, params);
    }

}
