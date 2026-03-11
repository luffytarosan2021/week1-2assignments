import java.util.*;

public  class UsernameAvailibilityChecker{
    HashMap<String,Integer>usernameMap=new HashMap<>();
    HashMap<String, Integer>attemptCount=new HashMap<>();
    public boolean checkAvailibility(String username){
        attemptCount.put(username,attemptCount.getOrDefault(username,0)+1);
        return !usernameMap.containsKey(username);
    }
    public void registerUser(String username,int userId){
        usernameMap.put(username,userId);
    }
    public List<String>suggestAlternatives(String Username){
        List<String>suggestions= new ArrayList<>();
        suggestions.add(Username+"1");
        suggestions.add(Username+"2");
        suggestions.add(Username.replace("_","."));
        return suggestions;
    }
    public String getMostAttempted(){
        String maxUser="";
        int maxCount=0;
        for(String user:attemptCount.keySet()){
            if(attemptCount.get(user)>maxCount){
                maxCount=attemptCount.get(user);
                maxUser=user;
            }
        }
        return maxUser+"("+maxCount+"attempts)";
    }


    public static void main(String[]args){
        UsernameAvailibilityChecker system=new UsernameAvailibilityChecker();
        system.registerUser("Anjan",101);
        System.out.println(system.checkAvailibility("Anjan"));
        System.out.println(system.checkAvailibility("Diya"));
        System.out.println(system.suggestAlternatives("Anjan"));
        System.out.println(system.getMostAttempted());
    }
}