#!/bin/bash                                                                                                                                                                                                                                        
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
is_process_running() {                                                                                                                                                                                                                             
 if [[ -n $(pgrep $1) ]];                                                                                                                                                                                                                          
 then                                                                                                                                                                                                                                              
  return 0                                                                                                                                                                                                                                         
 else                                                                                                                                                                                                                                              
  return 1                                                                                                                                                                                                                                         
 fi                                                                                                                                                                                                                                                
}                                                                                                                                                                                                                                                  
                                                                                                                                                                                                                                                   
stop_container() {                                                                                                                                                                                                                                 
 echo 'received SIGTERM'                                                                                                                                                                                                                           
 /usr/sbin/rpc.nfsd 0                                                                                                                                                                                                                              
 sleep 1                                                                                                                                                                                                                                           
 exit                                                                                                                                                                                                                                              
}                                                                                                                                                                                                                                                  
                                                                                                                                                                                                                                                   
trap stop_container SIGTERM                                                                                                                                                                                                                        
                                                                                                                                                                                                                                                   
                                                                                                                                                                                                                                                   
if ! is_process_running 'rpcbind'; then                                                                                                                                                                                                            
 echo 'starting rpcbind'                                                                                                                                                                                                                           
 /sbin/rpcbind -i                                                                                                                                                                                                                                  
fi                                                                                                                                                                                                                                                 
                                                                                                                                                                                                                                                   
if ! is_process_running 'rpc.statd'; then                                                                                                                                                                                                          
 echo 'starting rpc.statd'                                                                                                                                                                                                                         
 /sbin/rpc.statd --no-notify --port 32765 --outgoing-port 32766                                                                                                                                                                                    
 sleep .5                                                                                                                                                                                                                                          
fi

if [[ ! -a /proc/fs/nfsd/threads ]]; then                                                                                                                                                                                                          
 echo 'starting rpc.nfsd'                                                                                                                                                                                                                          
 echo $(pgrep 'nfsd')                                                                                                                                                                                                                              
 /usr/sbin/rpc.nfsd -V3 -N2 -N4 -d 8                                                                                                                                                                                                               
fi                                                                                                                                                                                                                                                 
                                                                                                                                                                                                                                                   
if ! is_process_running 'rpc.mountd'; then                                                                                                                                                                                                         
 echo 'starting rpc.mountd'                                                                                                                                                                                                                        
 /usr/sbin/rpc.mountd -V3 -N2 -N4 --port 32767                                                                                                                                                                                                     
 /usr/sbin/exportfs -ra                                                                                                                                                                                                                            
fi                                                                                                                                                                                                                                                 
                                                                                                                                                                                                                                                   
sleep 1                                                                                                                                                                                                                                            
                                                                                                                                                                                                                                                   
echo "TARGET_HOST:"$TARGET_HOST
echo "TARGET_DIR:"$TARGET_DIR
echo "LOCAL_DIR:"$LOCAL_DIR
mount $TARGET_HOST:$TARGET_DIR $LOCAL_DIR
if [ $? -ne 0 ]; then
    echo "mount failed"
else
    echo "mount succeed"
    while :
    do
     sleep 5
    done
fi
                                                                                                                                                                                                                                                   
