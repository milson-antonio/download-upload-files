import com.milsondev.downloaduploadfiles.db.entity.MyFile;
import com.milsondev.downloaduploadfiles.db.repository.MyFileRepository;
import com.milsondev.downloaduploadfiles.service.MyFileService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class MyTest {

    @Mock
    private MyFileRepository myFileRepository;

    @InjectMocks
    private MyFileService myFileService;

    public MyTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddFile() {
        // Criar um arquivo mock
        MultipartFile mockFile = mock(MultipartFile.class);

        // Simular o comportamento do método getSize() para retornar um tipo long
        when(mockFile.getOriginalFilename()).thenReturn("arquivo.txt");
        when(mockFile.getSize()).thenReturn(100L); // Exemplo de tamanho fictício

        // Chamar o método addFile
        myFileService.addFile(mockFile);

        // Verificar se o método save do myFileRepository foi chamado com o myFile correto
        verify(myFileRepository, times(1)).save(any(MyFile.class)); // Verificar se qualquer MyFile foi passado
    }

}