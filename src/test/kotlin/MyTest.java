import com.milsondev.downloaduploadfiles.api.Category;
import com.milsondev.downloaduploadfiles.db.entity.MyFile;
import com.milsondev.downloaduploadfiles.db.repository.MyFileRepository;
import com.milsondev.downloaduploadfiles.service.MyFileService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class MyTest {
    @Mock
    private MyFileRepository myFileRepository;
    @InjectMocks
    private MyFileService myFileService;
    @Value("${max.file.size:1048576}")
    private long MAX_FILE_SIZE;
    @Value("${max.number.of.files:10}")
    private long MAX_NUMBER_OF_FILES;

    public MyTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddFile() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.getOriginalFilename()).thenReturn("arquivo.txt");
        when(mockFile.getSize()).thenReturn(1L);

        myFileService.saveFile(mockFile, Category.CV);

        verify(myFileRepository, times(1)).save(any(MyFile.class)); // Verificar se qualquer MyFile foi passado
    }

}