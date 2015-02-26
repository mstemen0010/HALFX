/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package halfx;


import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author Mathias
 */
class Indicator implements Runnable {
    
      enum IndicatorType
    {
        Undefined,
        Pwr,
        Com,
        Lok,
        Lop,
        Mem;
    }
      
    enum IndicateState 
    {
        On,
        Off;                
    }
    
    private final ImageView indicatorImageView;
    private final Stack<Image> indicateStack = new Stack<>();    
    // private final ArrayList<Image> imageArray = new ArrayList<>(2);
    boolean running = true;
    boolean indicating = false;
    Iterator<Image> it = null;
    final short shortIndicate = 2000;
    final short longIndicate = 5000;
    long indicateDelay = longIndicate;
    boolean bootSequence = false;
    boolean showIndicate = false;      
    IndicatorType myIndicatorType = null;
    String myTypeName = null;

    public synchronized void showBootIndicate( )
    {
        this.indicateDelay = longIndicate;
        bootSequence = true;
    }
    
    public synchronized void boot()
    {
        showBootSequence( );
    }
            
    public Indicator( IndicatorType type, ImageView viewToIndicate, Image indicateOn, Image indicateOff ) {

        indicatorImageView = viewToIndicate;
        this.myIndicatorType = type;
        myTypeName = type.toString();
        this.indicateStack.add( indicateOn );
        this.indicateStack.add( indicateOff );
        viewToIndicate.setImage(indicateOff);

    }

    public synchronized void toggleIndicate() {
         if( ! showIndicate )
             return;
        indicating = !indicating;
        Image currentState;
        if( indicating ){
            currentState = this.indicateStack.elementAt(IndicateState.On.ordinal());
        }
        else
        {
           currentState = this.indicateStack.elementAt(IndicateState.Off.ordinal());
        }
        indicatorImageView.setImage(currentState);
    }

    private synchronized void showBootSequence( )
    {
        System.out.println("Start Boot for: " + this.myTypeName );
        try {            
            final Image currentStateOn = this.indicateStack.elementAt(IndicateState.On.ordinal());
            final Image currentStateOff = this.indicateStack.elementAt(IndicateState.Off.ordinal());
            indicatorImageView.setImage(currentStateOn);            
            Thread.sleep(this.shortIndicate);
            indicatorImageView.setImage(currentStateOff);
            Thread.sleep(this.shortIndicate);
            indicatorImageView.setImage(currentStateOn);            
            Thread.sleep(this.longIndicate);
            indicatorImageView.setImage(currentStateOff);
            Thread.sleep(this.shortIndicate);
            indicatorImageView.setImage(currentStateOn);            
            Thread.sleep(this.shortIndicate);
            indicatorImageView.setImage(currentStateOff);                                                            
            bootSequence = false;
        } catch (InterruptedException ex) {
            Logger.getLogger(Indicator.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("End Boot for: " + this.myTypeName );
    }
    
    @Override
    public void run() {
//        it = imageArray.iterator();
        Image throbImage;
        while (running) {
            while (indicating) {
                if (bootSequence) {
                    showBootSequence();
                } else {
                    toggleIndicate();
                }

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Indicator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public synchronized void stop() {
        this.indicatorImageView.setImage( this.indicateStack.elementAt(IndicateState.Off.ordinal()));
        indicating = false;
        running = false;
    }

//    public void addImage(Image newImageToAdd) {
//        imageArray.add(newImageToAdd);
//    }
}
  
public class HALFXMLController implements Initializable {
    Image pwrOn, pwrOff;
    Image comOn, comOff;
    Image memOn, memOff;
    Image lopOn, lopOff;
    Image lokOn, lokOff;
    Indicator comIndicator;
    Indicator memIndicator;
    Indicator lopIndicator;
    Indicator lokIndicator;
    
    @FXML
    ImageView Pwr_Image;
    @FXML 
    ImageView Com_Image;
    @FXML
    ImageView Mem_Image;
    @FXML
    ImageView Lok_Image;
    @FXML
    ImageView Lop_Image;
    @FXML
    Button pwr_Button;
    @FXML
    Button tlk_Send_Button;
    
     
    Thread comIndThread, memIndThread, lopIndThread, lokIndThread;
    
    enum IndicatorType
    {
        Undefined,
        Pwr,
        Com,
        Lok,
        Lop,
        Mem;
    }
    
    enum IndicatorState
    {
        Undefined,
        On,
        Off;
    }
    
    IndicatorState comState = IndicatorState.Undefined;
    IndicatorState memState = IndicatorState.Undefined;
    IndicatorState lopState = IndicatorState.Undefined;
    IndicatorState lokState = IndicatorState.Undefined;
    
    IndicatorState pwrState = IndicatorState.Off;
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        comState = IndicatorState.Undefined;
        
        this.comOn = new Image("/halfx/images/Com_1.jpg", true);
        this.comOff = new Image("halfx/images/Com_Off_1.jpg", true);
        this.memOn = new Image("/halfx/images/Mem_On_1.jpg", true);
        this.memOff = new Image("/halfx/images/Mem_Off_1.jpg", true);
        this.lopOn = new Image("/halfx/images/Lop_On_1.jpg", true);
        this.lopOff = new Image("/halfx/images/Lop_Off_1.jpg", true);
        this.memOn = new Image("/halfx/images/Lok_1.jpg", true);
        this.memOff = new Image("/halfx/images/Lok_Off_1.jpg", true);
        
        
        this.pwrOn = new Image("/halfx/images/Orange_Pwr_On.jpg", true);
        this.pwrOff = new Image("halfx/images/Orange_Pwr_Off.jpg", true);
        comIndicator = new Indicator( Indicator.IndicatorType.Com, this.Com_Image, comOn, comOff );
        memIndicator = new Indicator( Indicator.IndicatorType.Mem, this.Mem_Image, memOn, memOff );
        lopIndicator = new Indicator( Indicator.IndicatorType.Lop, this.Lop_Image, lopOn, lopOff );
        lokIndicator = new Indicator( Indicator.IndicatorType.Lok, this.Lok_Image, lokOn, lokOff );
        
        comState = IndicatorState.Off;
        memState = IndicatorState.Off;
        lopState = IndicatorState.Off;
        lokState = IndicatorState.Off;
        
        this.setIndicator(IndicatorType.Com, comState);
        this.setIndicator(IndicatorType.Mem, memState);
        this.setIndicator(IndicatorType.Lop, lopState);
        this.setIndicator(IndicatorType.Lok, lokState);
                                            
    }    

    private void boot()
    {
        System.out.println("Start Boot...");
               
        this.comIndicator.showBootIndicate();        
        this.comIndThread = new Thread( comIndicator );
        this.comIndThread.start();
        comIndicator.boot();
                
        this.memIndicator.showBootIndicate();        
        this.memIndThread = new Thread( memIndicator );
        this.memIndThread.start();
        memIndicator.boot();
               
        this.lopIndicator.showBootIndicate();        
        this.lopIndThread = new Thread( lopIndicator );
        this.lopIndThread.start();
        lopIndicator.boot();

        this.lokIndicator.showBootIndicate();        
        this.lokIndThread = new Thread( lokIndicator );
        this.lokIndThread.start();
        lokIndicator.boot();
        
        System.out.println("End Boot");
    }
    @FXML
    public void togglePwr()
    {        
        if( ! this.pwrState.equals(IndicatorState.On) )
        {
            pwrState = IndicatorState.On;  
            setIndicator( IndicatorType.Pwr, pwrState );
            init_brain();
            boot();
        }
        else
        {            
            pwrState = IndicatorState.Off; 
            setIndicator( IndicatorType.Pwr, pwrState );
            setAllIndicateOff();
            
        }         
        
    }
    
    private void init_brain()
    {
        this.pwrState = IndicatorState.On;
        pushToTalk();
    }
    
    @FXML
    public void pushToTalk()
    {
        if( ! pwrState.equals( IndicatorState.On ))
            return;
        // toggle the com state
        if( ! this.comState.equals(IndicatorState.On) )
            comState = IndicatorState.On;
        else
            comState = IndicatorState.Off;
        setIndicator( IndicatorType.Com, comState );        
    }
    
    private void setIndicator(IndicatorType typeToSet, IndicatorState indicateState )
    {
        
        switch( typeToSet )
        {
            case Com:
                switch( indicateState )
                {
                    case On:
                        this.Com_Image.setImage(comOn);
                        break;
                    case Off:
                        this.Com_Image.setImage(comOff);
                        break;                           
                }
                break;
            case Mem:                 
                switch( indicateState )
                {
                    case On:
                        this.Mem_Image.setImage(memOn);
                        break;
                    case Off:
                        this.Mem_Image.setImage(memOff);
                        break;                           
                }
                break;
            case Lok:
                switch( indicateState )
                {
                    case On:
                        this.Lok_Image.setImage(lokOn);
                        break;
                    case Off:
                        this.Lok_Image.setImage(lokOff);
                        break;                           
                }                
                break;
            case Lop:
                switch( indicateState )
                {
                    case On:
                        this.Lop_Image.setImage(lopOn);
                        break;
                    case Off:
                        this.Lop_Image.setImage(lopOff);
                        break;                           
                }                
                
                break;
            case Pwr:
                switch( indicateState )
                {
                    case On:
                        this.Pwr_Image.setImage(pwrOn);
                        break;
                    case Off:
                        this.Pwr_Image.setImage(pwrOff);
                        break;
                }
            break;
        }
    }
    
    private void setAllIndicateOff()
    {
          this.comIndicator.stop();
          this.memIndicator.stop();
          this.lokIndicator.stop();
          this.lopIndicator.stop();                          
    }
    
}
