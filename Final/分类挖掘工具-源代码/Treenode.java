package sheepTools;

public class Treenode{
	int tr,tl,x,qq,state,miss;
	double itt,alpha;
	int[] set;  //set[0]记录set集合中样本个数
	public Treenode(){
		this.tr=0;
		this.tl=0;
		this.x=0;
		this.qq=0;
		this.itt=0;
		this.state=0;
		this.alpha=1;
		this.miss=0;
	}
}
