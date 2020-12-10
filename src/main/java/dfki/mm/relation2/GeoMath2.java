package dfki.mm.relation2;

//import de.dfki.streetlife.osmTypes.MyNode;
//import de.dfki.streetlife.relation.MyNode;

//import dfki.mm.tracks.GPSPoint;

import java.util.ArrayList;

public class GeoMath2
{
    // http://www.movable-type.co.uk/scripts/latlong.html
    // --------------------------------------------------
	
	private static float R = 6371; // world radius in km
	
	
	public static double computeDistance(MyNode node1, MyNode node2) {
		return computeDistance(node1.getLat(),	node1.getLon(),	node2.getLat(),	node2.getLon());
	}

	public static double computeDistance(double lat1, double lon1, double lat2, double lon2) {
		lat1 = Math.toRadians(lat1);
		lon1 = Math.toRadians(lon1);
		lat2 = Math.toRadians(lat2);
		lon2 = Math.toRadians(lon2);

		double dLat = lat2-lat1;
		double dLon = lon2-lon1;

		double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double d = R * c;

		return d;
	}

	/**
     * Distance between 2 points
     */
	public static double computeDistance(MyNode node1, double lat, double lon) {
		return computeDistance(node1.getLat(),	node1.getLon(),	lat,	lon);
	}

	public static MyNode getNearestPoint(MyNode referencePoint, ArrayList<MyNode> pointList)
	{
        MyNode nearestPoint = null;
        double closestDistance = Double.MAX_VALUE;
        
        for(MyNode point: pointList)
        {                	
        	double currentDistance = computeDistance(referencePoint, point);
        	
        	if(currentDistance < closestDistance)
        	{
        		nearestPoint = point;
        		closestDistance = currentDistance;
        	}
        }
		return nearestPoint;
	}

    /**
     * (the along-track distance is the distance from the start point to the closest point on the path to the third point)
     */
	public static double computeAlongTrackDistance(MyNode start, MyNode end, MyNode point)
	{
		double d13 = computeDistance(start, point);
		double dXt = computeCrossTrackDistance(start, end, point) ;
		double dAt = Math.acos(Math.cos(d13/R)/Math.cos(dXt/R)) * R;
		
		return dAt;
	}
	
	
	private static double computeCrossTrackDistance(MyNode start, MyNode end, MyNode point)
	{
		double d13 = computeDistance(start, point);
		double bearing13 = computeBearingRad(start, point);
		double bearing12 = computeBearingRad(start, end);
		double dXt = Math.asin(Math.sin(d13/R)*Math.sin(bearing13-bearing12)) * R;
		
		return dXt;
	}


	public static double computeBearingRad(MyNode fromNode, MyNode toNode) {
		return  computeBearingRad(fromNode.getLat(),fromNode.getLon(),toNode.getLat(),toNode.getLon());
	}

//	public static double computeBearingRad(GPSPoint fromNode, GPSPoint toNode) {
//		return  computeBearingRad(fromNode.gps_latitude,fromNode.gps_longitude,toNode.gps_latitude,toNode.gps_longitude);
//	}

	/**
	 * @return -pi..pi
	 */
	public static double computeBearingRad(double fromLat, double fromLon, double toLat, double toLon) {
		double lat1 = (double) Math.toRadians(fromLat);
		double lon1 = (double) Math.toRadians(fromLon);
		double lat2 = (double) Math.toRadians(toLat);
		double lon2 = (double) Math.toRadians(toLon);
		
		double y = (double) (Math.sin(lon2-lon1) * Math.cos(lat2));
		double x = (double) (Math.cos(lat1)*Math.sin(lat2) - Math.sin(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1));
		double bearingRadian = (double) Math.atan2(y, x);
		
		return bearingRadian;
	}
	
	
	public static MyNode computeIntermediatePoint(MyNode fromNode, MyNode toNode, double f)
	{
		double d = computeDistance(fromNode, toNode);

		double lat1 = (double) Math.toRadians(fromNode.getLat());
		double lon1 = (double) Math.toRadians(fromNode.getLon());
		double lat2 = (double) Math.toRadians(toNode.getLat());
		double lon2 = (double) Math.toRadians(toNode.getLon());

		double A = (double) (Math.sin((1-f)*d)/Math.sin(d));
		double B = (double) (Math.sin(f*d)/Math.sin(d));
		double x = (double) (A*Math.cos(lat1)*Math.cos(lon1) +  B*Math.cos(lat2)*Math.cos(lon2));
		double y = (double) (A*Math.cos(lat1)*Math.sin(lon1) +  B*Math.cos(lat2)*Math.sin(lon2));
		double z = (double) (A*Math.sin(lat1)+B*Math.sin(lat2));

		double lat = (double) Math.toDegrees(Math.atan2(z,Math.sqrt(x*x+y*y)));
		double lon = (double) Math.toDegrees(Math.atan2(y,x));

		return new MyNode(-1).setLatLon((double) lat, (double) lon);
	}
}
